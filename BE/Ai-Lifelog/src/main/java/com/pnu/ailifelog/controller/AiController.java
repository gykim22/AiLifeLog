package com.pnu.ailifelog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    // private final LLMConvertService llmConvertService;

    // @PostMapping("/convert/daily-snapshot")
    // public ChatResponse convertDailySnapshot(@RequestBody DailySnapshot dailySnapshot) {
    //     return llmConvertService.convertDailySnapshot(dailySnapshot);
    // }

    // @PostMapping("/convert/diary")
    // public ChatResponse convertDiary(@RequestBody Diary diary) {
    //     return llmConvertService.convertDiary(diary);
    // }

}
