package com.pnu.ailifelog.dto.user;

import com.pnu.ailifelog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ResUserDto is a Data Transfer Object (DTO) that represents a user in the response.
 * It contains fields such as id, loginId, name, password, and email.
 * This DTO is used to transfer user data between different layers of the application.
 */
@Getter
@AllArgsConstructor
public class ResUserDto {
    final private UUID id;
    final private String loginId;
    final private String name;
    final private LocalDateTime createdAt;
    final private LocalDateTime updatedAt;

    public static ResUserDto fromEntity(User user) {
        return new ResUserDto(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
