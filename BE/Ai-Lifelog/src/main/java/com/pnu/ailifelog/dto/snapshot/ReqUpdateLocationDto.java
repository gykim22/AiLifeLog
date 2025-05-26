package com.pnu.ailifelog.dto.snapshot;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateLocationDto {
    @Size(min = 1, max = 50, message = "위치 태그는 1자 이상 50자 이하로 입력하세요.")
    private String tagName;

    private Double latitude;
    private Double longitude;
} 