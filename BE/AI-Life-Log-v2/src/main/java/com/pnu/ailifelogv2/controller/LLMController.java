package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.LLM.ReqUserPrompt;
import com.pnu.ailifelogv2.dto.LLM.ResAskDto;
import com.pnu.ailifelogv2.dto.LLM.ResGenDto;
import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/llms")
@RequiredArgsConstructor
public class LLMController {
    private final LLMService llmService;

    @PostMapping("/ask")
    public ResponseEntity<ResAskDto> summarize(@RequestBody ReqUserPrompt prompt, @AuthenticationPrincipal User user) {
        String summary = llmService.askToLLM(prompt.getPrompt(), user);
        return new ResponseEntity<>(new ResAskDto(summary), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<ResGenDto> generate(@RequestBody ReqUserPrompt prompt, @AuthenticationPrincipal User user) {
        List<LifeLogOutput> lifeLogOutputs = llmService.generateFromLLM(prompt.getPrompt(), user);
        return new ResponseEntity<>(new ResGenDto(lifeLogOutputs), HttpStatus.OK);
    }
}
