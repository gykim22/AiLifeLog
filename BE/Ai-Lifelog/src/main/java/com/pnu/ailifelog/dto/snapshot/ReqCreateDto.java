package com.pnu.ailifelog.dto.snapshot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReqCreateDto {
    @NotBlank(message = "스냅샷 내용을 입력하세요.")
    @Size(min = 1, max = 1000, message = "스냅샷 내용은 1자 이상 1000자 이하로 입력하세요.")
    final private String content;
    
    @NotBlank(message = "위치 태그를 입력하세요.")
    @Size(min = 1, max = 50, message = "위치 태그는 1자 이상 50자 이하로 입력하세요.")
    final private String locationTag;

    // Optional latitude and longitude for location
    private Double latitude;
    private Double longitude;

    public ReqCreateDto(String content, String locationTag) {
        this.content = content;
        this.locationTag = locationTag;
        this.latitude = null;
        this.longitude = null;
    }

    public ReqCreateDto(String content, String locationTag, Double latitude, Double longitude) {
        this.content = content;
        this.locationTag = locationTag;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
