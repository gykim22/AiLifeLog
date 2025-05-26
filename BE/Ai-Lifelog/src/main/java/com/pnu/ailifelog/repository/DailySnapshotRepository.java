package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySnapshotRepository extends JpaRepository<DailySnapshot, UUID> {
    Optional<DailySnapshot> findByDateAndUser(LocalDate date, User user);
    
    // 사용자별 DailySnapshot 조회 (최신순) - 페이지네이션
    Page<DailySnapshot> findByUserOrderByDateDesc(User user, Pageable pageable);
    
    // 사용자별 DailySnapshot 조회 (최신순) - 리스트
    List<DailySnapshot> findByUserOrderByDateDesc(User user);
}
