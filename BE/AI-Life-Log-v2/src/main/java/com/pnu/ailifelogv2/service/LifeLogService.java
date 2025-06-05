package com.pnu.ailifelogv2.service;

import com.pnu.ailifelogv2.dto.LifeLog.ReqCreateLifeLogDto;
import com.pnu.ailifelogv2.dto.LifeLog.ReqUpdateLifeLogDto;
import com.pnu.ailifelogv2.dto.LifeLog.ResLifeLogDto;
import com.pnu.ailifelogv2.entity.LifeLog;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.repository.LifeLogRepository;
import com.pnu.ailifelogv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LifeLogService {
    private final LifeLogRepository lifeLogRepository;
    private final UserRepository userRepository;

    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "타임스탬프 형식이 잘못되었습니다. 'YYYY-MM-DDTHH:MM:SS' 형식으로 입력해주세요.");
        }

    }


    public Page<ResLifeLogDto> getLifeLogsByUser(User user, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return lifeLogRepository.findByUser(user,pageable).map(ResLifeLogDto::new);
    }

    public Page<ResLifeLogDto> getLifeLogsByUserAndDateRange(User user, LocalDate from, LocalDate to, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        return lifeLogRepository.findByTimestampBetweenAndUser(from.atStartOfDay(), from.atTime(23, 59, 59), user, pageable)
                .map(ResLifeLogDto::new);
    }

    public ResLifeLogDto getLifeLogById(Long id, User user) {
        LifeLog lifelog = lifeLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 ID의 LifeLog가 존재하지 않습니다."
                ));
        return new ResLifeLogDto(lifelog);
    }

    public ResLifeLogDto createLifeLog(ReqCreateLifeLogDto reqLifeLogDto, User user) {
        LifeLog lifeLog = LifeLog.builder()
                .title(reqLifeLogDto.getTitle())
                .description(reqLifeLogDto.getDescription())
                .timestamp(parseTimestamp(reqLifeLogDto.getTimestamp()))
                .user(user)
                .build();
        return new ResLifeLogDto(lifeLogRepository.save(lifeLog));
    }

    public LifeLog updateLifeLog(Long id, ReqUpdateLifeLogDto reqLifeLogDto, User user) {
        LifeLog existingLifeLog = lifeLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 ID의 LifeLog가 존재하지 않습니다."
                ));
        if (reqLifeLogDto.getTitle() != null) existingLifeLog.setTitle(reqLifeLogDto.getTitle());
        if (reqLifeLogDto.getDescription() != null) existingLifeLog.setDescription(reqLifeLogDto.getDescription());
        if (reqLifeLogDto.getTimestamp() != null) {
            existingLifeLog.setTimestamp(parseTimestamp(reqLifeLogDto.getTimestamp()));
        }

        return lifeLogRepository.save(existingLifeLog);
    }

    public void deleteLifeLog(Long id, User user) {
        LifeLog lifeLog = lifeLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 ID의 LifeLog가 존재하지 않습니다."
                ));
        lifeLogRepository.delete(lifeLog);
    }

    @Transactional
    public List<ResLifeLogDto> createLifeLogs(List<ReqCreateLifeLogDto> lifeLogDtos, User user) {
        List<ResLifeLogDto> resLifeLogDtoList = new ArrayList<>();
        for (ReqCreateLifeLogDto lifeLogDto : lifeLogDtos) {
            resLifeLogDtoList.add(createLifeLog(lifeLogDto, user));
        }
        return resLifeLogDtoList;
    }
}
