package com.pnu.ailifelog.service;

import com.pnu.ailifelog.dto.diary.ResDiaryDto;
import com.pnu.ailifelog.entity.Diary;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * 일기(Diary) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 이 클래스는 일기의 생성, 조회, 수정, 삭제 등의 기능을 제공합니다.
 * 
 * @author Swallow Lee
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {
    
    private final DiaryRepository diaryRepository;

    /**
     * 새로운 일기를 생성합니다.
     *
     * @param title 일기 제목
     * @param content 일기 내용
     * @param date 일기 날짜
     * @param user 일기를 작성하는 사용자
     * @return 생성된 일기
     */
    public Diary createDiary(String title, String content, LocalDate date, User user) {
        try {
            // 같은 날짜에 이미 일기가 있는지 확인
            Optional<Diary> existingDiary = diaryRepository.findByDateAndUser(date, user);
            if (existingDiary.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "해당 날짜에 이미 일기가 존재합니다. 기존 일기를 수정하거나 다른 날짜를 선택해주세요.");
            }
            
            // 새 일기 생성
            Diary diary = new Diary();
            diary.setTitle(title);
            diary.setContent(content);
            diary.setDate(date);
            diary.setUser(user);
            
            Diary savedDiary = diaryRepository.save(diary);
            log.info("일기 생성 성공: 사용자={}, 날짜={}, 제목={}", user.getLoginId(), date, title);
            
            return savedDiary;
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 생성 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자의 모든 일기를 페이지네이션하여 조회합니다.
     *
     * @param user 사용자
     * @param pageable 페이지네이션 정보
     * @return 일기 페이지
     */
    @Transactional(readOnly = true)
    public Page<Diary> getAllDiariesByUser(User user, Pageable pageable) {
        try {
            return diaryRepository.findByUserOrderByDateDesc(user, pageable);
        } catch (Exception e) {
            log.error("사용자 일기 목록 조회 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 일기를 ID로 조회합니다.
     *
     * @param diaryId 일기 ID
     * @param user 사용자 (권한 확인용)
     * @return 조회된 일기
     */
    @Transactional(readOnly = true)
    public Diary getDiaryById(UUID diaryId, User user) {
        try {
            Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일기를 찾을 수 없습니다."));
            
            // 권한 확인 - 본인의 일기만 조회 가능
            if (!diary.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 일기에 접근할 수 없습니다.");
            }
            
            return diary;
            
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
     * @param user 사용자
     * @return 해당 날짜의 일기 (없으면 null)
     */
    @Transactional(readOnly = true)
    public Diary getDiaryByDate(LocalDate date, User user) {
        try {
            return diaryRepository.findByDateAndUser(date, user).orElse(null);
        } catch (Exception e) {
            log.error("날짜별 일기 조회 실패: 날짜={}, 사용자={}, 오류={}", date, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "날짜별 일기 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 기간별 일기를 페이지네이션하여 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param user 사용자
     * @param pageable 페이지네이션 정보
     * @return 해당 기간의 일기 페이지
     */
    @Transactional(readOnly = true)
    public Page<Diary> getDiariesByDateRange(LocalDate startDate, LocalDate endDate, User user, Pageable pageable) {
        try {
            if (startDate.isAfter(endDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작 날짜가 종료 날짜보다 늦을 수 없습니다.");
            }
            
            return diaryRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate, pageable);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("기간별 일기 조회 실패: 기간={}-{}, 사용자={}, 오류={}", 
                startDate, endDate, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "기간별 일기 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 키워드로 일기를 검색합니다 (제목 또는 내용에서 검색).
     *
     * @param keyword 검색 키워드
     * @param user 사용자
     * @param pageable 페이지네이션 정보
     * @return 검색된 일기 페이지
     */
    @Transactional(readOnly = true)
    public Page<Diary> searchDiaries(String keyword, User user, Pageable pageable) {
        try {
            return diaryRepository.findByUserAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByDateDesc(
                user, keyword, keyword, pageable);
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
     * @param title 새로운 제목 (선택적)
     * @param content 새로운 내용 (선택적)
     * @param date 새로운 날짜 (선택적)
     * @param user 사용자 (권한 확인용)
     * @return 수정된 일기
     */
    public Diary updateDiary(UUID diaryId, String title, String content, LocalDate date, User user) {
        try {
            Diary diary = getDiaryById(diaryId, user); // 권한 확인 포함
            
            // 날짜 변경 시 중복 확인
            if (date != null && !date.equals(diary.getDate())) {
                Optional<Diary> existingDiary = diaryRepository.findByDateAndUser(date, user);
                if (existingDiary.isPresent() && !existingDiary.get().getId().equals(diaryId)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, 
                        "해당 날짜에 이미 다른 일기가 존재합니다.");
                }
                diary.setDate(date);
            }
            
            // 필드 업데이트 (null이 아닌 경우만)
            if (title != null && !title.trim().isEmpty()) {
                diary.setTitle(title);
            }
            if (content != null && !content.trim().isEmpty()) {
                diary.setContent(content);
            }
            
            Diary updatedDiary = diaryRepository.save(diary);
            log.info("일기 수정 성공: ID={}, 사용자={}", diaryId, user.getLoginId());
            
            return updatedDiary;
            
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
     * @param user 사용자 (권한 확인용)
     */
    public void deleteDiary(UUID diaryId, User user) {
        try {
            Diary diary = getDiaryById(diaryId, user); // 권한 확인 포함
            
            diaryRepository.delete(diary);
            log.info("일기 삭제 성공: ID={}, 사용자={}", diaryId, user.getLoginId());
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("일기 삭제 실패: ID={}, 사용자={}, 오류={}", diaryId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일기 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * Diary 엔티티를 ResDiaryDto로 변환합니다.
     *
     * @param diary 변환할 Diary 엔티티
     * @return 변환된 ResDiaryDto
     */
    public ResDiaryDto convertToDto(Diary diary) {
        return ResDiaryDto.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .date(diary.getDate())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }
} 