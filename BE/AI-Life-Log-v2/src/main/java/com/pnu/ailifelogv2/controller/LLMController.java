package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.LLM.ReqAskDto;
import com.pnu.ailifelogv2.dto.LLM.ReqGenDto;
import com.pnu.ailifelogv2.dto.LLM.ResAskDto;
import com.pnu.ailifelogv2.dto.LLM.ResGenDto;
import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import com.pnu.ailifelogv2.entity.LifeLog;
import com.pnu.ailifelogv2.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/llms")
@RequiredArgsConstructor
public class LLMController {
    private final LLMService llmService;

    @PostMapping("/ask")
    public ResponseEntity<ResAskDto> summarize(@RequestBody ReqAskDto prompt, Authentication authentication) {
        String summary = llmService.askToLLM(prompt.getPrompt(), authentication);
        return new ResponseEntity<>(new ResAskDto(summary), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<ResGenDto> generate(@RequestBody ReqGenDto genDto, Authentication authentication) {
        if (genDto.getPrompt() == null || genDto.getPrompt().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프롬프트를 입력하세요!");
        }
        if (genDto.getDate() == null) {
            genDto.setDate(LocalDate.now());
        }
        List<LifeLogOutput> lifeLogs = llmService.generateFromLLM(genDto.getDate(), genDto.getPrompt() , authentication);
        return new ResponseEntity<>(new ResGenDto(lifeLogs), HttpStatus.OK);
    }
}
