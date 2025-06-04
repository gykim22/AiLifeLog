package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.LifeLog.ReqCreateLifeLogDto;
import com.pnu.ailifelogv2.dto.LifeLog.ReqUpdateLifeLogDto;
import com.pnu.ailifelogv2.dto.LifeLog.ResLifeLogDto;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.service.LifeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    private void validateUserExists(User user) {
        if (user == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."
            );
        }
    }

    @GetMapping
    public ResponseEntity<Page<ResLifeLogDto>> getAllLifeLogs (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @AuthenticationPrincipal User user) {

        validatePageAndSize(page, size);
        validateUserExists(user);

        if (from != null && to != null) {
            return  ResponseEntity.ok(
                lifeLogService.getLifeLogsByUserAndDateRange(user, from, to, page, size)
            );
        }
        return ResponseEntity.ok(
            lifeLogService.getLifeLogsByUser(user, page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResLifeLogDto> getLifeLogById (
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        validateUserExists(user);
        ResLifeLogDto lifeLog = lifeLogService.getLifeLogById(id, user);
        return ResponseEntity.ok(lifeLog);
    }

    @PostMapping
    public ResponseEntity<ResLifeLogDto> createLifeLog (
            @RequestBody ReqCreateLifeLogDto lifeLogDto,
            @AuthenticationPrincipal User user) {
        validateUserExists(user);
        if (lifeLogDto.getTimestamp() == null || lifeLogDto.getTitle() == null || lifeLogDto.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "타임스탬프, 제목, 설명은 필수 항목입니다.");
        }
        return new ResponseEntity<>(
            new ResLifeLogDto(lifeLogService.createLifeLog(lifeLogDto, user)),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResLifeLogDto> updateLifeLog (
            @PathVariable Long id,
            @RequestBody ReqUpdateLifeLogDto lifeLogDto,
            @AuthenticationPrincipal User user) {
        validateUserExists(user);
        if (lifeLogDto.getTimestamp() == null && lifeLogDto.getTitle() == null && lifeLogDto.getDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 하나의 필드(타임스탬프, 제목, 설명)는 업데이트해야 합니다.");
        }
        return new ResponseEntity<>(
            new ResLifeLogDto(lifeLogService.updateLifeLog(id, lifeLogDto, user)),
            HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLifeLog (
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        validateUserExists(user);
        lifeLogService.deleteLifeLog(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
