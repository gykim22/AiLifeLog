package com.pnu.ailifelogv2.dto.LifeLog;

import com.pnu.ailifelogv2.entity.LifeLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ResLifeLogDto {
    private Long id;
    private String title;
    private String description;
    private String timestamp;

    public ResLifeLogDto(LifeLog lifeLog) {
        this.id = lifeLog.getId();
        this.title = lifeLog.getTitle();
        this.description = lifeLog.getDescription();
        this.timestamp = lifeLog.getTimestamp().toString();
    }
}
