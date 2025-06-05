package com.pnu.ailifelogv2.repository;

import com.pnu.ailifelogv2.entity.LifeLog;
import com.pnu.ailifelogv2.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LifeLogRepository extends JpaRepository<LifeLog, Long> {
    Page<LifeLog> findByUser(User user, Pageable pageable);
    Optional<LifeLog> findByIdAndUser(Long id, User user);
    Page<LifeLog> findByTimestampBetweenAndUser(LocalDateTime start, LocalDateTime end, User user, Pageable pageable);
    List<LifeLog> saveAll(List<LifeLog> lifeLogs);
}
