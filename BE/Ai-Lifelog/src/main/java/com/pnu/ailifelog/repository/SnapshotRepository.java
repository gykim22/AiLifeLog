package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.Snapshot;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
    
    // 사용자별 스냅샷 조회 (최신순)
    List<Snapshot> findByDailySnapshot_UserOrderByTimestampDesc(User user);
    
    // 특정 DailySnapshot의 스냅샷들 조회 (시간순)
    List<Snapshot> findByDailySnapshotOrderByTimestampAsc(DailySnapshot dailySnapshot);
    
    // 특정 위치의 스냅샷들 조회 (최신순)
    List<Snapshot> findByLocationOrderByTimestampDesc(Location location);
    
    // 기간별 스냅샷 조회 (최신순)
    List<Snapshot> findByDailySnapshot_UserAndDailySnapshot_DateBetweenOrderByTimestampDesc(
        User user, LocalDate startDate, LocalDate endDate);
}
