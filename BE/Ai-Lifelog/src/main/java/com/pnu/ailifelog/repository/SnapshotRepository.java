package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.Snapshot;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
    
    // 사용자별 스냅샷 조회 (최신순) - 페이지네이션
    Page<Snapshot> findByDailySnapshot_UserOrderByTimestampDesc(User user, Pageable pageable);
    
    // 사용자별 스냅샷 조회 (최신순) - 리스트
    List<Snapshot> findByDailySnapshot_UserOrderByTimestampDesc(User user);
    
    // 특정 DailySnapshot의 스냅샷들 조회 (시간순)
    List<Snapshot> findByDailySnapshotOrderByTimestampAsc(DailySnapshot dailySnapshot);
    
    // 특정 위치의 스냅샷들 조회 (최신순) - 페이지네이션
    Page<Snapshot> findByLocationOrderByTimestampDesc(Location location, Pageable pageable);
    
    // 특정 위치의 스냅샷들 조회 (최신순) - 리스트
    List<Snapshot> findByLocationOrderByTimestampDesc(Location location);
    
    // 기간별 스냅샷 조회 (최신순) - 페이지네이션
    Page<Snapshot> findByDailySnapshot_UserAndDailySnapshot_DateBetweenOrderByTimestampDesc(
        User user, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // 기간별 스냅샷 조회 (최신순) - 리스트
    List<Snapshot> findByDailySnapshot_UserAndDailySnapshot_DateBetweenOrderByTimestampDesc(
        User user, LocalDate startDate, LocalDate endDate);
}
