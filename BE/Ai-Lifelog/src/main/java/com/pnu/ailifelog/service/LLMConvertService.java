package com.pnu.ailifelog.service;

import com.pnu.ailifelog.component.EntityTemplater;
import com.pnu.ailifelog.component.tool.UserLocationTools;
import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.Diary;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.DailySnapshotRepository;
import com.pnu.ailifelog.repository.DiaryRepository;
import com.pnu.ailifelog.repository.LocationRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class LLMConvertService {
    private final DailySnapshotRepository dailySnapshotRepository;
    private final LocationRepository locationRepository;
    private final DiaryRepository diaryRepository;
    private final ChatClient chatClient;
    private final TrackTokenUsageService trackTokenUsageService;

    private final EntityTemplater entityTemplater;

    private final String dailySnapshotSystemTemplate;
    private final String dailySnapshotUserTemplate;

    private final String diarySystemTemplate;
    private final String diaryUserTemplate;

    private static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }



    public LLMConvertService(OpenAiChatModel openAiChatModel,
         DailySnapshotRepository dailySnapshotRepository,
        LocationRepository locationRepository,
        DiaryRepository diaryRepository,
        EntityTemplater entityTemplater,
        TrackTokenUsageService trackTokenUsageService,
        @Value("classpath:template/DailySnapshotSystemTemplate.txt") Resource dailySnapshotSystemTemplateFile,
        @Value("classpath:template/DailySnapshotUserTemplate.txt") Resource dailySnapshotUserTemplateFile,
        @Value("classpath:template/DiarySystemTemplate.txt") Resource diarySystemTemplateFile,
        @Value("classpath:template/DiaryUserTemplate.txt") Resource diaryUserTemplateFile

    ) {
        this.chatClient = ChatClient.create(openAiChatModel);

        this.dailySnapshotRepository = dailySnapshotRepository;
        this.diaryRepository = diaryRepository;
        this.locationRepository = locationRepository;

        this.entityTemplater = entityTemplater;
        this.trackTokenUsageService = trackTokenUsageService;

        this.dailySnapshotSystemTemplate = asString(dailySnapshotSystemTemplateFile);
        this.dailySnapshotUserTemplate = asString(dailySnapshotUserTemplateFile);

        this.diarySystemTemplate = asString(diarySystemTemplateFile);
        this.diaryUserTemplate = asString(diaryUserTemplateFile);
    }

    public ChatResponse toDiary(UUID dailySnapshotId, User owner) {
        DailySnapshot dailySnapshot = dailySnapshotRepository.findById(dailySnapshotId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일간 스냅샷을 찾을 수 없습니다.")
        );
        if (!dailySnapshot.getUser().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 일간 스냅샷에 대한 권한이 없습니다.");
        }
        String formattedSnapshot = entityTemplater.templateDailySnapshot(dailySnapshot);
        ChatResponse res=  chatClient.prompt().system(dailySnapshotSystemTemplate)
                .user(
                input -> input.text(dailySnapshotUserTemplate).params(Map.of("format", formattedSnapshot))
        ).call().chatResponse();
        trackTokenUsageService.saveUserTokenUsage(owner, LocalDate.now(), res.getMetadata().getUsage());
        return res;
    }

    public ChatResponse toDailySnapShot(UUID diaryId, User owner) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일기를 찾을 수 없습니다.")
        );

        if (!diary.getUser().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 일기에 대한 권한이 없습니다.");
        }

        ChatResponse res = chatClient.prompt().system(diarySystemTemplate)
                .user(input -> input.text(diaryUserTemplate)
                    .params(Map.of(
                            "title", dailySnapshotUserTemplate,
                            "date", diary.getDate(),
                            "content", diary.getContent()
                    )))
                .tools(new UserLocationTools(locationRepository, owner))
                .call().chatResponse();
        trackTokenUsageService.saveUserTokenUsage(owner, LocalDate.now(), res.getMetadata().getUsage());
        return res;
    }
}
