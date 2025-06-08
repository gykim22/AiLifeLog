package com.pnu.ailifelog.controller;

import com.pnu.ailifelog.dto.snapshot.*;
import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.Snapshot;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.service.SnapshotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 스냅샷 관련 API를 처리하는 컨트롤러입니다.
 * 이 컨트롤러는 스냅샷 생성, 조회, 수정, 삭제 등의 기능을 제공합니다.
 *
 * @author Swallow Lee
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/snapshots")
@RequiredArgsConstructor
public class SnapShotController {
    
    private final SnapshotService snapshotService;

    /**
     * 새로운 스냅샷을 생성합니다.
     *
     * @param reqCreateDto 스냅샷 생성 요청 DTO
     * @param user 인증된 사용자
     * @return 생성된 스냅샷
     */
    @PostMapping
    public ResponseEntity<Snapshot> createSnapshot(
            @Valid @RequestBody ReqCreateDto reqCreateDto,
            @AuthenticationPrincipal User user) {
        try {
            Snapshot createdSnapshot = snapshotService.createSnapshot(
                reqCreateDto.getContent(),
                null, // timestamp는 현재 시간 사용
                reqCreateDto.getLocationTag(),
                reqCreateDto.getLatitude(),
                reqCreateDto.getLongitude(),
                user
            );
            log.info("스냅샷 생성 성공: 사용자={}, 내용={}", user.getLoginId(), reqCreateDto.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSnapshot);
        } catch (Exception e) {
            log.error("스냅샷 생성 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 시간으로 스냅샷을 생성합니다.
     *
     * @param reqCreateWithTimeDto 시간 포함 스냅샷 생성 요청 DTO
     * @param user 인증된 사용자
     * @return 생성된 스냅샷
     */
    @PostMapping("/with-time")
    public ResponseEntity<Snapshot> createSnapshotWithTime(
            @Valid @RequestBody ReqCreateWithTimeDto reqCreateWithTimeDto,
            @AuthenticationPrincipal User user) {
        try {
            Snapshot createdSnapshot = snapshotService.createSnapshot(
                reqCreateWithTimeDto.getContent(),
                reqCreateWithTimeDto.getTimestamp(),
                reqCreateWithTimeDto.getLocationTag(),
                reqCreateWithTimeDto.getLatitude(),
                reqCreateWithTimeDto.getLongitude(),
                user
            );
            log.info("시간 지정 스냅샷 생성 성공: 사용자={}, 시간={}", user.getLoginId(), reqCreateWithTimeDto.getTimestamp());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSnapshot);
        } catch (Exception e) {
            log.error("시간 지정 스냅샷 생성 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자의 모든 스냅샷을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return 스냅샷 페이지
     */
    @GetMapping
    public ResponseEntity<Page<Snapshot>> getAllSnapshots(
            @PageableDefault(size = 20, sort = "timestamp") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            Page<Snapshot> snapshots = snapshotService.getAllSnapshotsByUser(user, pageable);
            return ResponseEntity.ok(snapshots);
        } catch (Exception e) {
            log.error("스냅샷 조회 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 스냅샷을 ID로 조회합니다.
     *
     * @param snapshotId 스냅샷 ID
     * @param user 인증된 사용자
     * @return 조회된 스냅샷
     */
    @GetMapping("/{snapshotId}")
    public ResponseEntity<Snapshot> getSnapshotById(
            @PathVariable UUID snapshotId,
            @AuthenticationPrincipal User user) {
        try {
            Snapshot snapshot = snapshotService.getSnapshotById(snapshotId, user);
            return ResponseEntity.ok(snapshot);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("스냅샷 조회 실패: ID={}, 사용자={}, 오류={}", snapshotId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 날짜의 스냅샷들을 조회합니다.
     *
     * @param date 조회할 날짜
     * @param user 인증된 사용자
     * @return 해당 날짜의 스냅샷 리스트
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Snapshot>> getSnapshotsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        try {
            List<Snapshot> snapshots = snapshotService.getSnapshotsByDateAndUser(date, user);
            return ResponseEntity.ok(snapshots);
        } catch (Exception e) {
            log.error("날짜별 스냅샷 조회 실패: 날짜={}, 사용자={}, 오류={}", date, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "날짜별 스냅샷 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 기간의 스냅샷들을 페이지네이션하여 조회합니다.
     *
     * @param reqDateRangeDto 날짜 범위 요청 DTO
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return 해당 기간의 스냅샷 페이지
     */
    @PostMapping("/range")
    public ResponseEntity<Page<Snapshot>> getSnapshotsByDateRange(
            @Valid @RequestBody ReqDateRangeDto reqDateRangeDto,
            @PageableDefault(size = 20, sort = "timestamp") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            Page<Snapshot> snapshots = snapshotService.getSnapshotsByDateRangeAndUser(
                reqDateRangeDto.getStartDate(), reqDateRangeDto.getEndDate(), user, pageable);
            return ResponseEntity.ok(snapshots);
        } catch (Exception e) {
            log.error("기간별 스냅샷 조회 실패: 기간={}-{}, 사용자={}, 오류={}", 
                reqDateRangeDto.getStartDate(), reqDateRangeDto.getEndDate(), user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "기간별 스냅샷 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 위치의 스냅샷들을 페이지네이션하여 조회합니다.
     *
     * @param locationTag 위치 태그명
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return 해당 위치의 스냅샷 페이지
     */
    @GetMapping("/location/{locationTag}")
    public ResponseEntity<Page<Snapshot>> getSnapshotsByLocation(
            @PathVariable String locationTag,
            @PageableDefault(size = 20, sort = "timestamp") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            Page<Snapshot> snapshots = snapshotService.getSnapshotsByLocationAndUser(locationTag, user, pageable);
            return ResponseEntity.ok(snapshots);
        } catch (Exception e) {
            log.error("위치별 스냅샷 조회 실패: 위치={}, 사용자={}, 오류={}", 
                locationTag, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치별 스냅샷 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 스냅샷을 수정합니다.
     *
     * @param snapshotId 수정할 스냅샷 ID
     * @param reqUpdateSnapshotDto 스냅샷 수정 요청 DTO
     * @param user 인증된 사용자
     * @return 수정된 스냅샷
     */
    @PutMapping("/{snapshotId}")
    public ResponseEntity<Snapshot> updateSnapshot(
            @PathVariable UUID snapshotId,
            @Valid @RequestBody ReqUpdateSnapshotDto reqUpdateSnapshotDto,
            @AuthenticationPrincipal User user) {
        try {
            Snapshot updatedSnapshot = snapshotService.updateSnapshot(
                snapshotId, 
                reqUpdateSnapshotDto.getContent(), 
                reqUpdateSnapshotDto.getTimestamp(), 
                reqUpdateSnapshotDto.getLocationTag(), 
                reqUpdateSnapshotDto.getLatitude(), 
                reqUpdateSnapshotDto.getLongitude(), 
                user);
            log.info("스냅샷 수정 성공: ID={}, 사용자={}", snapshotId, user.getLoginId());
            return ResponseEntity.ok(updatedSnapshot);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("스냅샷 수정 실패: ID={}, 사용자={}, 오류={}", snapshotId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 수정 중 오류가 발생했습니다.");
        }
    }

    /**
     * 스냅샷을 삭제합니다.
     *
     * @param snapshotId 삭제할 스냅샷 ID
     * @param user 인증된 사용자
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{snapshotId}")
    public ResponseEntity<Void> deleteSnapshot(
            @PathVariable UUID snapshotId,
            @AuthenticationPrincipal User user) {
        try {
            snapshotService.deleteSnapshot(snapshotId, user);
            log.info("스냅샷 삭제 성공: ID={}, 사용자={}", snapshotId, user.getLoginId());
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("스냅샷 삭제 실패: ID={}, 사용자={}, 오류={}", snapshotId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자의 DailySnapshot들을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @param user 인증된 사용자
     * @return DailySnapshot 페이지
     */
    @GetMapping("/daily")
    public ResponseEntity<Page<DailySnapshot>> getDailySnapshots(
            @PageableDefault(size = 10, sort = "date") Pageable pageable,
            @AuthenticationPrincipal User user) {
        try {
            Page<DailySnapshot> dailySnapshots = snapshotService.getDailySnapshotsByUser(user, pageable);
            return ResponseEntity.ok(dailySnapshots);
        } catch (Exception e) {
            log.error("일별 스냅샷 조회 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일별 스냅샷 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자의 위치 목록을 조회합니다.
     *
     * @param user 인증된 사용자
     * @return 위치 리스트
     */
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getLocations(@AuthenticationPrincipal User user) {
        try {
            List<Location> locations = snapshotService.getLocationsByUser(user);
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            log.error("위치 목록 조회 실패: 사용자={}, 오류={}", user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 위치 정보를 수정합니다.
     *
     * @param locationId 수정할 위치 ID
     * @param reqUpdateLocationDto 위치 수정 요청 DTO
     * @param user 인증된 사용자
     * @return 수정된 위치
     */
    @PutMapping("/locations/{locationId}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long locationId,
            @Valid @RequestBody ReqUpdateLocationDto reqUpdateLocationDto,
            @AuthenticationPrincipal User user) {
        try {
            Location updatedLocation = snapshotService.updateLocation(
                locationId, 
                reqUpdateLocationDto.getTagName(), 
                reqUpdateLocationDto.getLatitude(), 
                reqUpdateLocationDto.getLongitude(), 
                user);
            log.info("위치 수정 성공: ID={}, 사용자={}", locationId, user.getLoginId());
            return ResponseEntity.ok(updatedLocation);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("위치 수정 실패: ID={}, 사용자={}, 오류={}", locationId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치 수정 중 오류가 발생했습니다.");
        }
    }

    /**
     * 위치를 삭제합니다.
     *
     * @param locationId 삭제할 위치 ID
     * @param user 인증된 사용자
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long locationId,
            @AuthenticationPrincipal User user) {
        try {
            snapshotService.deleteLocation(locationId, user);
            log.info("위치 삭제 성공: ID={}, 사용자={}", locationId, user.getLoginId());
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("위치 삭제 실패: ID={}, 사용자={}, 오류={}", locationId, user.getLoginId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치 삭제 중 오류가 발생했습니다.");
        }
    }
}
