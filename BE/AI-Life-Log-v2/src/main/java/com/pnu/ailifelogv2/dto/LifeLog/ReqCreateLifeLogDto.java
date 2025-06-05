package com.pnu.ailifelogv2.dto.LifeLog;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class ReqCreateLifeLogDto {
    private String title;
    private String description;
    private String timestamp;

    public ReqCreateLifeLogDto(String title, String description) {
        this.title = title;
        this.description = description;
        this.timestamp = LocalDateTime.now().toString();
    }

    public ReqCreateLifeLogDto(String title, String description, String timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }
}
