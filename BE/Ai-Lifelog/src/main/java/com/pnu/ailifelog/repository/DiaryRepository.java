package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.Diary;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, UUID> {
    
    // 사용자별 일기 조회 (최신순) - 페이지네이션
    Page<Diary> findByUserOrderByDateDesc(User user, Pageable pageable);
    
    // 사용자별 일기 조회 (최신순) - 리스트
    List<Diary> findByUserOrderByDateDesc(User user);
    
    // 특정 날짜의 일기 조회
    Optional<Diary> findByDateAndUser(LocalDate date, User user);
    
    // 기간별 일기 조회 (최신순) - 페이지네이션
    Page<Diary> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // 기간별 일기 조회 (최신순) - 리스트
    List<Diary> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    
    // 제목으로 검색 (최신순) - 페이지네이션
    Page<Diary> findByUserAndTitleContainingIgnoreCaseOrderByDateDesc(User user, String title, Pageable pageable);
    
    // 내용으로 검색 (최신순) - 페이지네이션
    Page<Diary> findByUserAndContentContainingIgnoreCaseOrderByDateDesc(User user, String content, Pageable pageable);
    
    // 제목 또는 내용으로 검색 (최신순) - 페이지네이션
    Page<Diary> findByUserAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByDateDesc(
        User user, String title, String content, Pageable pageable);
} 