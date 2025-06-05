package com.pnu.ailifelogv2.dto.LLM;

import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResGenDto {
    List<LifeLogOutput> content;
}
