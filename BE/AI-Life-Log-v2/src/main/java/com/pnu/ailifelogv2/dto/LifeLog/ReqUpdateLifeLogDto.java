package com.pnu.ailifelogv2.dto.LifeLog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateLifeLogDto {
    private String title;
    private String description;
    private String timestamp;
}