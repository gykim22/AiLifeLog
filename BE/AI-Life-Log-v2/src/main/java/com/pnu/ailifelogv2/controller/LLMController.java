package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.LifeLog.LifeLogOutput;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/llms")
@RequiredArgsConstructor
public class LLMController {
    private final LLMService llmService;

    @PostMapping("/ask")
    public ResponseEntity<String> summarize(String userInput, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(llmService.askToLLM(userInput, user), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<LifeLogOutput>> generate(String userInput, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(llmService.generateFromLLM(userInput, user), HttpStatus.OK);
    }
}
