package com.pnu.ailifelogv2.dto.LifeLog;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"title", "description", "timestamp"})
public class LifeLogOutput {
    String title;
    String description;
    String timestamp;
}
