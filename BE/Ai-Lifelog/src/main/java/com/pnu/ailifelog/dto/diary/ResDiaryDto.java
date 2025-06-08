package com.pnu.ailifelog.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResDiaryDto {
    private UUID id;
    private String title;
    private String content;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 