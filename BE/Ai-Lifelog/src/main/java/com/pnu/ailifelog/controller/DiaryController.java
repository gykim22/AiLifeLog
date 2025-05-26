package com.pnu.ailifelog.controller;

import com.pnu.ailifelog.dto.diary.*;
import com.pnu.ailifelog.dto.snapshot.ReqDateRangeDto;
import com.pnu.ailifelog.entity.Diary;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 일기 관련 API를 처리하는 컨트롤러입니다.
 * 이 컨트롤러는 일기 생성, 조회, 수정, 삭제 등의 기능을 제공합니다.
 *
 * @author Swallow Lee
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {
    
    private final DiaryService diaryService;

    /**
     * 새로운 일기를 생성합니다.
     *
     * @param reqCreateDiaryDto 일기 생성 요청 DTO
     * @param user 인증된 사용자
     * @return 생성된 일기
     */
    @PostMapping
    public ResponseEntity<ResDiaryDto> createDiary(
            @Valid @RequestBody ReqCreateDiaryDto reqCreateDiaryDto,
            @AuthenticationPrincipal User user) {
        try {
            Diary createdDiary = diaryService.createDiary(
                reqCreateDiaryDto.getTitle(),
                reqCreateDiaryDto.getContent(),
                reqCreateDiaryDto.getDate(),
                user
            );
            ResDiaryDto responseDto = diaryService.convertToDto(createdDiary);
            log.info("일기 생성 성공: 사용자={}, 날짜={}", user.getLoginId(), reqCreateDiaryDto.getDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 생성 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자의 모든 일기를 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return 일기 페이지
     */
    @GetMapping
    public ResponseEntity<Page<ResDiaryDto>> getAllDiaries(
            @PageableDefault(size = 10, sort = "date") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            Page<Diary> diaries = diaryService.getAllDiariesByUser(user, pageable);
            Page<ResDiaryDto> responseDtos = diaries.map(diaryService::convertToDto);
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            log.error("일기 목록 조회 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 일기를 ID로 조회합니다.
     *
     * @param diaryId 일기 ID
     * @param user 인증된 사용자
     * @return 조회된 일기
     */
    @GetMapping("/{diaryId}")
    public ResponseEntity<ResDiaryDto> getDiaryById(
            @PathVariable UUID diaryId,
            @AuthenticationPrincipal User user) {
        try {
            Diary diary = diaryService.getDiaryById(diaryId, user);
            ResDiaryDto responseDto = diaryService.convertToDto(diary);
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 조회 실패: ID={}, 사용자={}, 오류={}", diaryId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 날짜의 일기를 조회합니다.
     *
     * @param date 조회할 날짜
     * @param user 인증된 사용자
     * @return 해당 날짜의 일기 (없으면 404)
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<ResDiaryDto> getDiaryByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        try {
            Diary diary = diaryService.getDiaryByDate(date, user);
            if (diary == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 날짜의 일기를 찾을 수 없습니다.");
            }
            ResDiaryDto responseDto = diaryService.convertToDto(diary);
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("날짜별 일기 조회 실패: 날짜={}, 사용자={}, 오류={}", date, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "날짜별 일기 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 기간별 일기를 페이지네이션하여 조회합니다.
     *
     * @param reqDateRangeDto 날짜 범위 요청 DTO
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return 해당 기간의 일기 페이지
     */
    @PostMapping("/range")
    public ResponseEntity<Page<ResDiaryDto>> getDiariesByDateRange(
            @Valid @RequestBody ReqDateRangeDto reqDateRangeDto,
            @PageableDefault(size = 10, sort = "date") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            Page<Diary> diaries = diaryService.getDiariesByDateRange(
                reqDateRangeDto.getStartDate(), 
                reqDateRangeDto.getEndDate(), 
                user, 
                pageable);
            Page<ResDiaryDto> responseDtos = diaries.map(diaryService::convertToDto);
            return ResponseEntity.ok(responseDtos);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("기간별 일기 조회 실패: 기간={}-{}, 사용자={}, 오류={}", 
                reqDateRangeDto.getStartDate(), reqDateRangeDto.getEndDate(), user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "기간별 일기 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 키워드로 일기를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return 검색된 일기 페이지
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ResDiaryDto>> searchDiaries(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "date") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색 키워드를 입력해주세요.");
            }
            
            Page<Diary> diaries = diaryService.searchDiaries(keyword.trim(), user, pageable);
            Page<ResDiaryDto> responseDtos = diaries.map(diaryService::convertToDto);
            return ResponseEntity.ok(responseDtos);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 검색 실패: 키워드={}, 사용자={}, 오류={}", keyword, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 검색 중 오류가 발생했습니다.");
        }
    }

    /**
     * 일기를 수정합니다.
     *
     * @param diaryId 수정할 일기 ID
     * @param reqUpdateDiaryDto 일기 수정 요청 DTO
     * @param user 인증된 사용자
     * @return 수정된 일기
     */
    @PutMapping("/{diaryId}")
    public ResponseEntity<ResDiaryDto> updateDiary(
            @PathVariable UUID diaryId,
            @Valid @RequestBody ReqUpdateDiaryDto reqUpdateDiaryDto,
            @AuthenticationPrincipal User user) {
        try {
            Diary updatedDiary = diaryService.updateDiary(
                diaryId,
                reqUpdateDiaryDto.getTitle(),
                reqUpdateDiaryDto.getContent(),
                reqUpdateDiaryDto.getDate(),
                user
            );
            ResDiaryDto responseDto = diaryService.convertToDto(updatedDiary);
            log.info("일기 수정 성공: ID={}, 사용자={}", diaryId, user.getLoginId());
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 수정 실패: ID={}, 사용자={}, 오류={}", diaryId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 수정 중 오류가 발생했습니다.");
        }
    }

    /**
     * 일기를 삭제합니다.
     *
     * @param diaryId 삭제할 일기 ID
     * @param user 인증된 사용자
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable UUID diaryId,
            @AuthenticationPrincipal User user) {
        try {
            diaryService.deleteDiary(diaryId, user);
            log.info("일기 삭제 성공: ID={}, 사용자={}", diaryId, user.getLoginId());
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 삭제 실패: ID={}, 사용자={}, 오류={}", diaryId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 삭제 중 오류가 발생했습니다.");
        }
    }
}
