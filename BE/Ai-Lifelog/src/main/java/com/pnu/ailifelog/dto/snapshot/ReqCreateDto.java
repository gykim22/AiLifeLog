package com.pnu.ailifelog.dto.snapshot;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class ReqCreateDto {
    @NotEmpty
    final private String content;
    @NotEmpty
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
