package com.pnu.ailifelog.service;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.DailySnapshotRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
public class LLMConvertService {

    @Value("classpath:template/DailySnapshotSystemTemplate.txt")
    private String dailySnapshotSystemTemplate;
    @Value("classpath:template/DailySnapshotUserTemplate.txt")
    private String dailySnapshotUserTemplate;

    private final DailySnapshotRepository dailySnapshotRepository;
    private final OpenAiChatModel openAiChatModel;
    private final ChatClient chatClient;

    public LLMConvertService(OpenAiChatModel openAiChatModel, DailySnapshotRepository dailySnapshotRepository) {
        this.dailySnapshotRepository = dailySnapshotRepository;
        this.openAiChatModel = openAiChatModel;
        this.chatClient = ChatClient.create(openAiChatModel);
    }
    public ChatResponse summarize(UUID dailySnapshotId, User owner) {
        DailySnapshot dailySnapshot = dailySnapshotRepository.findById(dailySnapshotId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일간 스냅샷을 찾을 수 없습니다.")
        );
        if (!dailySnapshot.getUser().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 일간 스냅샷에 대한 권한이 없습니다.");
        }
        String formattedSnapshot = formatDailySnapshotForLLM(dailySnapshot);

        return chatClient.prompt().system(dailySnapshotSystemTemplate)
                .user(
                input -> input.text(dailySnapshotUserTemplate).params(Map.of("format", formattedSnapshot))
        ).call().chatResponse();
    }



    private String formatDailySnapshotForLLM(DailySnapshot dailySnapshot) {
        StringBuilder sb = new StringBuilder();
        sb.append("날짜: ").append(dailySnapshot.getDate()).append("\n");
        sb.append("일정 목록:\n");
        dailySnapshot.getSnapshots().forEach(snapshot -> {
            sb.append("- [").append(snapshot.getTimestamp().toLocalTime()).append("]: ").append(snapshot.getContent());
            if (snapshot.getLocation() != null) {
                sb.append(" (위치: ").append(snapshot.getLocation().getTagName());
                if (snapshot.getLocation().getLatitude() != null && snapshot.getLocation().getLongitude() != null) {
                    sb.append(", 위도: ").append(snapshot.getLocation().getLatitude())
                            .append(", 경도: ").append(snapshot.getLocation().getLongitude());
                }
            }
            sb.append("\n");
        });
        return sb.toString();
    }
}
