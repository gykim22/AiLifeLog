package com.pnu.ailifelogv2.repository;

import com.pnu.ailifelogv2.entity.LifeLog;
import com.pnu.ailifelogv2.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LifeLogRepository extends JpaRepository<LifeLog, Long> {
    Page<LifeLog> findByUser(User user, Pageable pageable);
    Page<LifeLog> findByTimestampBetweenAndUser(LocalDateTime start, LocalDateTime end, User user, Pageable pageable);

    // for ai search
    Page<LifeLog> findByTitleContainingAndUser(String title, User user, Pageable pageable);
    Page<LifeLog> findByDescriptionContainingAndUser(String description, User user, Pageable pageable);

}
