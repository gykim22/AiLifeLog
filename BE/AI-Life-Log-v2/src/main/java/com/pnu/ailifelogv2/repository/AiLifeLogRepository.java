package com.pnu.ailifelogv2.repository;

import com.pnu.ailifelogv2.entity.LifeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AiLifeLogRepository extends JpaRepository<LifeLog, Long> {
     List<LifeLog> findByUserId(Long userId);
     List<LifeLog> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);
     List<LifeLog> findByUserIdAndTitleContaining(Long userId, String keyword);
}
