package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySnapshotRepository extends JpaRepository<DailySnapshot, UUID> {
    Optional<DailySnapshot> findByDateAndUser(LocalDate date, User user);
}
