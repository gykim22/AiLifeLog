package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.LifeLog.ReqCreateLifeLogDto;
import com.pnu.ailifelogv2.dto.LifeLog.ReqUpdateLifeLogDto;
import com.pnu.ailifelogv2.dto.LifeLog.ResLifeLogDto;
import com.pnu.ailifelogv2.service.LifeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/logs")
public class LifeLogController {
    private final LifeLogService lifeLogService;

    private void validatePageAndSize(Integer page, Integer size) {
        if (page == null || size == null || page < 0 || size <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "페이지 번호와 크기는 0 이상의 정수여야 합니다."
            );
        }
    }

    @GetMapping
    public ResponseEntity<Page<ResLifeLogDto>> getAllLifeLogs (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication authentication) {
        validatePageAndSize(page, size);

        if (from != null && to != null) {
            return  ResponseEntity.ok(
                lifeLogService.getLifeLogsByUserAndDateRange(authentication, from, to, page, size)
            );
        }
        return ResponseEntity.ok(
            lifeLogService.getLifeLogsByUser(authentication, page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResLifeLogDto> getLifeLogById (
            @PathVariable Long id,
            Authentication authentication) {
        ResLifeLogDto lifeLog = lifeLogService.getLifeLogById(id, authentication);
        return ResponseEntity.ok(lifeLog);
    }

    @PostMapping
    public ResponseEntity<ResLifeLogDto> createLifeLog (
            @RequestBody ReqCreateLifeLogDto lifeLogDto,
            Authentication authentication) {
        if (lifeLogDto.getTimestamp() == null || lifeLogDto.getTitle() == null || lifeLogDto.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "타임스탬프, 제목, 설명은 필수 항목입니다.");
        }
        return new ResponseEntity<>(
            lifeLogService.createLifeLog(lifeLogDto, authentication),
            HttpStatus.CREATED
        );
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ResLifeLogDto>> createLifeLogs (
            @RequestBody ReqCreateLifeLogDto[] lifeLogDtos,
            Authentication authentication) {
        if (lifeLogDtos == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LifeLog DTO 리스트에 null 값이 포함되어 있습니다.");
        }
        List<ResLifeLogDto> createdLifeLogs = lifeLogService.createLifeLogs(lifeLogDtos, authentication);
        return new ResponseEntity<>(createdLifeLogs, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResLifeLogDto> updateLifeLog (
            @PathVariable Long id,
            @RequestBody ReqUpdateLifeLogDto lifeLogDto,
            Authentication authentication) {
        if (lifeLogDto.getTimestamp() == null && lifeLogDto.getTitle() == null && lifeLogDto.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 하나의 필드(타임스탬프, 제목, 설명)는 업데이트해야 합니다.");
        }
        return new ResponseEntity<>(
            new ResLifeLogDto(lifeLogService.updateLifeLog(id, lifeLogDto, authentication)),
            HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLifeLog (
            @PathVariable Long id,
            Authentication authentication) {
        lifeLogService.deleteLifeLog(id, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
