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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."
                ));
    }


    /**
     * 사용자의 LifeLog를 페이지네이션하여 가져옵니다.
     *
     * @param authentication 인증 정보
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이지네이션된 LifeLog 목록
     */
    public Page<ResLifeLogDto> getLifeLogsByUser(Authentication authentication, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        User user = getUserFromAuthentication(authentication);
        return lifeLogRepository.findByUser(user,pageable).map(ResLifeLogDto::new);
    }

    /**
     * 사용자의 LifeLog를 특정 날짜 범위로 페이지네이션하여 가져옵니다.
     *
     * @param authentication 인증 정보
     * @param from 시작 날짜
     * @param to 종료 날짜
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이지네이션된 LifeLog 목록
     */
    public Page<ResLifeLogDto> getLifeLogsByUserAndDateRange(Authentication authentication, LocalDate from, LocalDate to, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        User user = getUserFromAuthentication(authentication);
        return lifeLogRepository.findByTimestampBetweenAndUser(from.atStartOfDay(), to.atTime(23, 59, 59), user, pageable)
                .map(ResLifeLogDto::new);
    }

    /**
     * 특정 ID의 LifeLog를 가져옵니다.
     *
     * @param id LifeLog ID
     * @param authentication 인증 정보
     * @return LifeLog DTO
     */
    public ResLifeLogDto getLifeLogById(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        LifeLog lifelog = lifeLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 ID의 LifeLog가 존재하지 않습니다."
                ));
        return new ResLifeLogDto(lifelog);
    }

    /**
     * 새로운 LifeLog를 생성합니다.
     *
     * @param reqLifeLogDto 요청 DTO
     * @param authentication 인증 정보
     * @return 생성된 LifeLog DTO
     */
    public ResLifeLogDto createLifeLog(ReqCreateLifeLogDto reqLifeLogDto, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        LifeLog lifeLog = LifeLog.builder()
                .title(reqLifeLogDto.getTitle())
                .description(reqLifeLogDto.getDescription())
                .timestamp(parseTimestamp(reqLifeLogDto.getTimestamp()))
                .user(user)
                .build();
        return new ResLifeLogDto(lifeLogRepository.save(lifeLog));
    }

    /**
     * 특정 ID의 LifeLog를 업데이트합니다.
     *
     * @param id LifeLog ID
     * @param reqLifeLogDto 요청 DTO
     * @param authentication 인증 정보
     * @return 업데이트된 LifeLog
     */
    public LifeLog updateLifeLog(Long id, ReqUpdateLifeLogDto reqLifeLogDto, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
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

    /**
     * 특정 ID의 LifeLog를 삭제합니다.
     *
     * @param id LifeLog ID
     * @param authentication 인증 정보
     */
    public void deleteLifeLog(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        LifeLog lifeLog = lifeLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 ID의 LifeLog가 존재하지 않습니다."
                ));
        lifeLogRepository.delete(lifeLog);
    }

    /**
     * 여러 개의 LifeLog를 생성합니다.
     *
     * @param lifeLogDtos 요청 DTO 리스트
     * @param authentication 인증 정보
     * @return 생성된 LifeLog DTO 리스트
     */
    @Transactional
    public List<ResLifeLogDto> createLifeLogs(ReqCreateLifeLogDto[] lifeLogDtos, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<LifeLog> lifeLogs = new ArrayList<>();
        int count = 0;
        for (ReqCreateLifeLogDto lifeLogDto : lifeLogDtos) {
            count++;
            if (lifeLogDto.getTimestamp() == null || lifeLogDto.getTitle() == null || lifeLogDto.getDescription() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, count + "번째 항목 : 타임스탬프, 제목, 설명은 필수 항목입니다.");
            }
            LifeLog lifeLog = LifeLog.builder()
                    .title(lifeLogDto.getTitle())
                    .description(lifeLogDto.getDescription())
                    .timestamp(parseTimestamp(lifeLogDto.getTimestamp()))
                    .user(user)
                    .build();
            lifeLogs.add(lifeLog);
        }
        List<LifeLog> savedLifeLogs = lifeLogRepository.saveAll(lifeLogs);
        return savedLifeLogs.stream().map(ResLifeLogDto::new).collect(Collectors.toList());
    }
}
