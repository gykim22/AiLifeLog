package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.TokenUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageRepository extends JpaRepository<TokenUsage, Long> {

    // 특정 사용자에 대한 사용량 조회(최신순) - 페이지네이션
    List<TokenUsage> findByUserIdOrderByDateDesc(UUID userId);

    // 특정 사용자의 특정 날짜에 대한 사용량 조회
    Optional<TokenUsage> findByUserIdAndDate(UUID userId, LocalDate date);

    TokenUsage save(TokenUsage tokenUsage);
}
