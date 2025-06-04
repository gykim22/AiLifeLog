package com.pnu.ailifelog.service;

import com.pnu.ailifelog.entity.TokenUsage;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackTokenUsageService {
    private final UsageRepository usageRepository;

    public List<TokenUsage> getUserTokenUsage(User user) {
        // 사용자 토큰 사용량을 조회하는 로직
        return usageRepository.findByUserIdOrderByDateDesc(user.getId());
    }

    public TokenUsage getUserTokenUsageByDate(User user, LocalDate date) {
        // 특정 날짜의 사용자 토큰 사용량을 조회하는 로직
        return usageRepository.findByUserIdAndDate(user.getId(), date).orElse(null);
    }

    public TokenUsage saveUserTokenUsage(User user, LocalDate date, Usage usage) {
        // 사용자 토큰 사용량을 저장하는 로직
            TokenUsage presentTokenUsage = usageRepository.findByUserIdAndDate(user.getId(), date).orElse(null);
            if (presentTokenUsage != null) {
                // 이미 존재하는 사용량 업데이트
                presentTokenUsage.setTotalTokens(usage.getTotalTokens() + presentTokenUsage.getTotalTokens());
                presentTokenUsage.setPromptTokens(usage.getPromptTokens() + presentTokenUsage.getPromptTokens());
                presentTokenUsage.setCompletionTokens(usage.getCompletionTokens() + presentTokenUsage.getCompletionTokens());
                return usageRepository.save(presentTokenUsage);
            } else {
                // 새로운 사용량 생성
                TokenUsage newTokenUsage = new TokenUsage(user, date, usage);
                return usageRepository.save(newTokenUsage);
        }
    }


}
