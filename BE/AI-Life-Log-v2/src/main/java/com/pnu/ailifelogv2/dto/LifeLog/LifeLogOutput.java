package com.pnu.ailifelogv2.dto.LifeLog;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"title", "description", "timestamp"})
@Getter @Setter
@NoArgsConstructor
public class LifeLogOutput {
    String title;
    String description;
    String timestamp;
}
