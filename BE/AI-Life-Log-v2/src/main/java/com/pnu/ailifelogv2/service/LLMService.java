package com.pnu.ailifelogv2.service;

import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.repository.AiLifeLogRepository;
import com.pnu.ailifelogv2.repository.UserRepository;
import com.pnu.ailifelogv2.util.PromptTemplates;
import com.pnu.ailifelogv2.util.SummaryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LLMService {
    private final ChatClient chatClient;
    private final AiLifeLogRepository aiLifeLogRepository;
    private final UserRepository userRepository;
    private final BeanOutputConverter<List<LifeLogOutput>> outputConverter;

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."
                ));
    }


    @Autowired
    public LLMService(OpenAiChatModel chatModel,
                      AiLifeLogRepository aiLifeLogRepository,
                      UserRepository userRepository) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.aiLifeLogRepository = aiLifeLogRepository;
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

    public List<LifeLogOutput> generateFromLLM(String userInput, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return chatClient.prompt()
                .system(PromptTemplates.GEN_SYSTEM_PROMPT)
                .user(u -> u.text(PromptTemplates.GEN_USER_PROMPT)
                        .param("diaryText", userInput)).call()
                .entity(outputConverter);
    }
}
