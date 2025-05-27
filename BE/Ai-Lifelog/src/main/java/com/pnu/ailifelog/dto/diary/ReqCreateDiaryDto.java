package com.pnu.ailifelog.dto.diary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReqCreateDiaryDto {
    @NotBlank(message = "일기 제목을 입력하세요.")
    @Size(min = 1, max = 100, message = "일기 제목은 1자 이상 100자 이하로 입력하세요.")
    private String title;
    
    @NotBlank(message = "일기 내용을 입력하세요.")
    @Size(min = 1, max = 5000, message = "일기 내용은 1자 이상 5000자 이하로 입력하세요.")
    private String content;
    
    @NotNull(message = "일기 날짜를 입력하세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
} 