package com.pnu.ailifelogv2.service;

import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.repository.AiLifeLogRepository;
import com.pnu.ailifelogv2.repository.LifeLogRepository;
import com.pnu.ailifelogv2.util.PromptTemplates;
import com.pnu.ailifelogv2.util.SummaryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMService {
    private final ChatClient chatClient;
    private final AiLifeLogRepository aiLifeLogRepository;
    private final BeanOutputConverter<List<LifeLogOutput>> outputConverter;

    @Autowired
    public LLMService(OpenAiChatModel chatModel,
                      AiLifeLogRepository aiLifeLogRepository) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.aiLifeLogRepository = aiLifeLogRepository;
        this.outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {});
    }

    public String askToLLM(String userInput, User user) {
        return chatClient.prompt()
                .tools(new SummaryTools(aiLifeLogRepository, user.getId()))
                .system(PromptTemplates.ASK_SYSTEM_PROMPT)
                .user(userInput)
                .call().content();
    }

    public List<LifeLogOutput> generateFromLLM(String userInput, User user) {
        return chatClient.prompt()
                .system(PromptTemplates.GEN_SYSTEM_PROMPT)
                .user(u -> u.text(PromptTemplates.GEN_USER_PROMPT)
                        .param("diaryText", userInput)).call()
                .entity(outputConverter);
    }
}
