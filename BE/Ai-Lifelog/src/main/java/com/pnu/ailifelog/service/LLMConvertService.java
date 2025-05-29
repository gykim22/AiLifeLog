package com.pnu.ailifelog.service;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.DailySnapshotRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class LLMConvertService {
    private final DailySnapshotRepository dailySnapshotRepository;
    private final ChatClient chatClient;
    private final String dailySnapshotSystemTemplate;
    private final String dailySnapshotUserTemplate;

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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

    public LLMConvertService(OpenAiChatModel openAiChatModel,
                             DailySnapshotRepository dailySnapshotRepository,
                            @Value("classpath:template/DailySnapshotSystemTemplate.txt") Resource dailySnapshotSystemTemplateFile,
                            @Value("classpath:template/DailySnapshotUserTemplate.txt") Resource dailySnapshotUserTemplateFile
    ) {
        this.dailySnapshotRepository = dailySnapshotRepository;
        this.chatClient = ChatClient.create(openAiChatModel);
        this.dailySnapshotSystemTemplate = asString(dailySnapshotSystemTemplateFile);
        this.dailySnapshotUserTemplate = asString(dailySnapshotUserTemplateFile);
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
}
