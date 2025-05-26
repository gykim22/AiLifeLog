package com.pnu.ailifelog.dto.auth;

import com.pnu.ailifelog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ResSignupDto {
    final private UUID id;
    final private String username;
    final private String nickname;
    final private LocalDateTime createdAt;
    final private LocalDateTime updatedAt;

    public ResSignupDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
