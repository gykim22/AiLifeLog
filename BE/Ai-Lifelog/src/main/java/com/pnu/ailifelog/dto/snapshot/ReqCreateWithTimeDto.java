package com.pnu.ailifelog.dto.snapshot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReqCreateWithTimeDto {
    @NotBlank(message = "스냅샷 내용을 입력하세요.")
    @Size(min = 1, max = 1000, message = "스냅샷 내용은 1자 이상 1000자 이하로 입력하세요.")
    private String content;
    
    @NotNull(message = "시간을 입력하세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timestamp;
    
    @NotBlank(message = "위치 태그를 입력하세요.")
    @Size(min = 1, max = 50, message = "위치 태그는 1자 이상 50자 이하로 입력하세요.")
    private String locationTag;

    // Optional latitude and longitude for location
    private Double latitude;
    private Double longitude;
} 