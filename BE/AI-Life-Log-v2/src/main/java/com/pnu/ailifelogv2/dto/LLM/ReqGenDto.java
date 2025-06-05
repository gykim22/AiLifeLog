package com.pnu.ailifelogv2.dto.LLM;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter @Setter
public class ReqGenDto {
    String prompt;
    LocalDate date;
}
