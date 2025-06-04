package com.pnu.ailifelogv2.util;

import com.pnu.ailifelogv2.entity.LifeLog;
import com.pnu.ailifelogv2.repository.AiLifeLogRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 라이프로그 관련 AI 도구 모음 클래스입니다.
 */
public class SummaryTools {

    private final AiLifeLogRepository aiLifeLogRepository;
    private final Long userId;

    public SummaryTools(AiLifeLogRepository aiLifeLogRepository, Long userId) {
        this.aiLifeLogRepository = aiLifeLogRepository;
        this.userId = userId;
    }

    @Tool(
            name = "getCurrentTimeStamp",
            description = "현재 시간을 ISO 8601 형식(yyyy-MM-dd'T'HH:mm:ss)으로 반환합니다."
    )
    public String getCurrentTimeStamp() {
        return LocalDateTime.now().toString();
    }

    @Tool(
            name = "getLifeLogs",
            description = "사용자의 라이프로그를 지정한 기간(start ~ end) 내에서 조회합니다."
    )
    public List<LifeLog> getLifeLogs(
            @ToolParam(description = "조회 시작 timestamp, ISO 8601 형식 (예: 2024-06-01T00:00:00)") String start,
            @ToolParam(description = "조회 종료 timestamp, ISO 8601 형식 (예: 2024-06-07T23:59:59)") String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return aiLifeLogRepository.findByUserIdAndTimestampBetween(userId, startDate, endDate);
    }

    @Tool(
            name = "getLifeLogsByKeyword",
            description = "사용자의 라이프로그 중 제목에 특정 키워드가 포함된 항목을 조회합니다."
    )
    public List<LifeLog> getLifeLogsByKeyword(
            @ToolParam(description = "제목에 포함된 키워드") String keyword) {
        return aiLifeLogRepository.findByUserIdAndTitleContaining(userId, keyword);
    }

    @Tool(
            name = "getAllLifeLogs",
            description = "사용자의 모든 라이프로그를 조회합니다."
    )
    public List<LifeLog> getAllLifeLogs() {
        return aiLifeLogRepository.findByUserId(userId);
    }

    @Tool(
            name = "getRecentLifeLogs",
            description = "사용자의 최근 N개의 라이프로그를 조회합니다."
    )
    public List<LifeLog> getRecentLifeLogs(
            @ToolParam(description = "조회할 라이프로그 개수") int count) {
        return aiLifeLogRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(LifeLog::getTimestamp).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
