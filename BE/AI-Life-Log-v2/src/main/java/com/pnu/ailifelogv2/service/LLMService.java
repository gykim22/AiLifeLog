package com.pnu.ailifelogv2.service;

import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import com.pnu.ailifelogv2.entity.LifeLog;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.repository.AiLifeLogRepository;
import com.pnu.ailifelogv2.repository.LifeLogRepository;
import com.pnu.ailifelogv2.repository.UserRepository;
import com.pnu.ailifelogv2.util.PromptTemplates;
import com.pnu.ailifelogv2.util.SummaryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LLMService {
    private final ChatClient chatClient;
    private final AiLifeLogRepository aiLifeLogRepository;
    private final LifeLogRepository lifeLogRepository;
    private final UserRepository userRepository;
    private final BeanOutputConverter<List<LifeLogOutput>> outputConverter;

    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "타임스탬프 형식이 잘못되었습니다. 'YYYY-MM-DDTHH:MM:SS' 형식으로 입력해주세요.");
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."
                ));
    }

    private void saveLifeLogOutputs(List<LifeLogOutput> lifeLogOutputs, User user) {
        List<LifeLog> results = lifeLogOutputs.stream().map(
                output -> LifeLog.builder()
                        .title(output.getTitle())
                        .description(output.getDescription())
                        .timestamp(parseTimestamp(output.getTimestamp()))
                        .user(user)
                        .build()
        ).collect(Collectors.toList());
        lifeLogRepository.saveAll(results);
    }

    private List<LifeLogOutput> filterLifeLogs(List<LifeLogOutput> lifeLogOutputs) {
        return lifeLogOutputs.stream()
                .filter(Objects::nonNull)
                .filter(lifeLog -> lifeLog.getTitle() != null && !lifeLog.getTitle().isEmpty())
                .filter(lifeLog -> lifeLog.getDescription() != null && !lifeLog.getDescription().isEmpty())
                .collect(Collectors.toList());
    }

    @Autowired
    public LLMService(OpenAiChatModel chatModel,
                      AiLifeLogRepository aiLifeLogRepository,
                        LifeLogRepository lifeLogRepository,
                      UserRepository userRepository) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.aiLifeLogRepository = aiLifeLogRepository;
        this.lifeLogRepository = lifeLogRepository;
        this.userRepository = userRepository;
        this.outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {});
    }



    public String askToLLM(String userInput, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return chatClient.prompt()
                .tools(new SummaryTools(aiLifeLogRepository, user.getId()))
                .system(PromptTemplates.ASK_SYSTEM_PROMPT)
                .user(userInput)
                .call().content();
    }

    public List<LifeLogOutput> generateFromLLM(LocalDate date, String userInput, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Optional<LifeLog> latestLifeLog = lifeLogRepository.findFirstByTimestampBetweenAndUserOrderByTimestampDesc(
                LocalDateTime.of(date, LocalDateTime.MIN.toLocalTime()),
                LocalDateTime.of(date, LocalDateTime.MAX.toLocalTime()),
                user
        );
        String latestTimestamp = latestLifeLog.map(lifeLog -> lifeLog.getTimestamp().toString()).orElse("작성된 기록이 없습니다.");
        ChatResponse res =  chatClient.prompt()
                .system(PromptTemplates.GEN_SYSTEM_PROMPT)
                .user(u -> u.text(PromptTemplates.GEN_USER_PROMPT)
                        .params(Map.of(
                                "date", date.toString(),
                                "timestamp", latestTimestamp,
                                "diaryText", userInput,
                                "format", outputConverter.getFormat()
                        ))).call().chatResponse();


        List<LifeLogOutput> lifeLogOutputs;
        try {
            String content = res.getResult().getOutput().getText();
            lifeLogOutputs =  outputConverter.convert(content);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LLM 응답이 비어있습니다. 입력을 확인해주세요.");
        }
        if (lifeLogOutputs == null || lifeLogOutputs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LLM이 생성한 라이프로그가 없습니다. 입력을 확인해주세요.");
        }
        saveLifeLogOutputs(lifeLogOutputs, user);

        return lifeLogOutputs;
    }
}
