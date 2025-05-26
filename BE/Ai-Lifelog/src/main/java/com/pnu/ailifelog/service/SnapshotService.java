package com.pnu.ailifelog.service;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.Snapshot;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.DailySnapshotRepository;
import com.pnu.ailifelog.repository.LocationRepository;
import com.pnu.ailifelog.repository.SnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * 사용자 로그(Snapshot) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 이 클래스는 사용자 로그의 생성, 조회, 삭제 등의 기능을 제공합니다.
 * @author Swallow Lee
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SnapshotService {
    private final SnapshotRepository snapshotRepository;
    private final LocationRepository locationRepository;
    private final DailySnapshotRepository dailySnapshotRepository;

    /**
     * 새로운 스냅샷을 생성합니다.
     * 위치 태그를 기반으로 Location을 찾거나 생성하고, 해당 날짜의 DailySnapshot에 스냅샷을 추가합니다.
     *
     * @param content 스냅샷 내용
     * @param locationTag 위치 태그명
     * @param latitude GPS 위도 (선택적)
     * @param longitude GPS 경도 (선택적)
     * @param user 스냅샷을 생성하는 사용자
     * @return 생성된 스냅샷
     */
    public Snapshot createSnapshot(String content, String locationTag, Double latitude, Double longitude, User user) {
        try {
            // 1. 위치 정보 처리 - 기존 태그 찾거나 새로 생성
            Location location = findOrCreateLocation(locationTag, latitude, longitude, user);
            
            // 2. 오늘 날짜의 DailySnapshot 찾거나 생성
            LocalDate today = LocalDate.now();
            DailySnapshot dailySnapshot = findOrCreateDailySnapshot(today, user);
            
            // 3. 새 스냅샷 생성
            Snapshot snapshot = new Snapshot();
            snapshot.setContent(content);
            snapshot.setTimestamp(LocalDateTime.now());
            snapshot.setLocation(location);
            snapshot.setDailySnapshot(dailySnapshot);
            
            // 4. 스냅샷 저장
            Snapshot savedSnapshot = snapshotRepository.save(snapshot);
            
            return savedSnapshot;
            
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 위치 태그로 기존 Location을 찾거나 새로 생성합니다.
     *
     * @param locationTag 위치 태그명
     * @param latitude GPS 위도 (선택적)
     * @param longitude GPS 경도 (선택적)
     * @param user 사용자
     * @return 찾거나 생성된 Location
     */
    private Location findOrCreateLocation(String locationTag, Double latitude, Double longitude, User user) {
        // 사용자의 기존 위치 태그 찾기
        Optional<Location> existingLocation = locationRepository.findByTagNameAndUser(locationTag, user);
        
        if (existingLocation.isPresent()) {
            Location location = existingLocation.get();
            // GPS 정보가 제공되었고 기존 위치에 GPS 정보가 없다면 업데이트
            if (latitude != null && longitude != null && 
                (location.getLatitude() == null || location.getLongitude() == null)) {
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                return locationRepository.save(location);
            }
            return location;
        } else {
            // 새 위치 생성
            Location newLocation = Location.builder()
                    .tagName(locationTag)
                    .latitude(latitude)
                    .longitude(longitude)
                    .user(user)
                    .build();
            return locationRepository.save(newLocation);
        }
    }
    
    /**
     * 특정 날짜의 DailySnapshot을 찾거나 새로 생성합니다.
     *
     * @param date 날짜
     * @param user 사용자
     * @return 찾거나 생성된 DailySnapshot
     */
    private DailySnapshot findOrCreateDailySnapshot(LocalDate date, User user) {
        Optional<DailySnapshot> existingDailySnapshot = dailySnapshotRepository.findByDateAndUser(date, user);
        
        if (existingDailySnapshot.isPresent()) {
            return existingDailySnapshot.get();
        } else {
            // 새 DailySnapshot 생성
            DailySnapshot newDailySnapshot = new DailySnapshot();
            newDailySnapshot.setDate(date);
            newDailySnapshot.setUser(user);
            return dailySnapshotRepository.save(newDailySnapshot);
        }
    }

    /**
     * 특정 사용자의 모든 스냅샷을 최신순으로 조회합니다.
     *
     * @param user 조회할 사용자
     * @return 사용자의 모든 스냅샷 리스트 (최신순)
     */
    public List<Snapshot> getAllSnapshotsByUser(User user) {
        try {
            return snapshotRepository.findByDailySnapshot_UserOrderByTimestampDesc(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 날짜의 사용자 스냅샷들을 시간순으로 조회합니다.
     *
     * @param date 조회할 날짜
     * @param user 조회할 사용자
     * @return 해당 날짜의 스냅샷 리스트 (시간순)
     */
    public List<Snapshot> getSnapshotsByDateAndUser(LocalDate date, User user) {
        try {
            Optional<DailySnapshot> dailySnapshot = dailySnapshotRepository.findByDateAndUser(date, user);
            
            if (dailySnapshot.isPresent()) {
                return snapshotRepository.findByDailySnapshotOrderByTimestampAsc(dailySnapshot.get());
            } else {
                return List.of(); // 빈 리스트 반환
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "날짜별 스냅샷 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 위치의 사용자 스냅샷들을 최신순으로 조회합니다.
     *
     * @param locationTag 위치 태그명
     * @param user 조회할 사용자
     * @return 해당 위치의 스냅샷 리스트 (최신순)
     */
    public List<Snapshot> getSnapshotsByLocationAndUser(String locationTag, User user) {
        try {
            Optional<Location> location = locationRepository.findByTagNameAndUser(locationTag, user);
            
            if (location.isPresent()) {
                return snapshotRepository.findByLocationOrderByTimestampDesc(location.get());
            } else {
                return List.of(); // 빈 리스트 반환
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치별 스냅샷 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 기간의 사용자 스냅샷들을 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param user 조회할 사용자
     * @return 해당 기간의 스냅샷 리스트 (최신순)
     */
    public List<Snapshot> getSnapshotsByDateRangeAndUser(LocalDate startDate, LocalDate endDate, User user) {
        try {
            return snapshotRepository.findByDailySnapshot_UserAndDailySnapshot_DateBetweenOrderByTimestampDesc(
                user, startDate, endDate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "기간별 스냅샷 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 스냅샷을 ID로 조회합니다.
     *
     * @param snapshotId 스냅샷 ID
     * @param user 조회하는 사용자 (권한 확인용)
     * @return 조회된 스냅샷
     */
    public Snapshot getSnapshotById(UUID snapshotId, User user) {
        try {
            Snapshot snapshot = snapshotRepository.findById(snapshotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "스냅샷을 찾을 수 없습니다: " + snapshotId));
            
            // 권한 확인 - 본인의 스냅샷인지 체크
            if (!snapshot.getDailySnapshot().getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "해당 스냅샷에 접근할 권한이 없습니다.");
            }
            
            return snapshot;
        } catch (ResponseStatusException e) {
            throw e; // 이미 처리된 예외는 그대로 전달
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 사용자의 DailySnapshot들을 최신순으로 조회합니다.
     *
     * @param user 조회할 사용자
     * @return 사용자의 DailySnapshot 리스트 (최신순)
     */
    public List<DailySnapshot> getDailySnapshotsByUser(User user) {
        try {
            return dailySnapshotRepository.findByUserOrderByDateDesc(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "일별 스냅샷 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 사용자의 위치 태그들을 조회합니다.
     *
     * @param user 조회할 사용자
     * @return 사용자의 위치 리스트
     */
    public List<Location> getLocationsByUser(User user) {
        try {
            return locationRepository.findByUserOrderByTagNameAsc(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 스냅샷을 수정합니다.
     * content, timestamp, location만 수정 가능합니다.
     *
     * @param snapshotId 수정할 스냅샷 ID
     * @param newContent 새로운 내용 (선택적)
     * @param newTimestamp 새로운 시간 (선택적)
     * @param newLocationTag 새로운 위치 태그 (선택적)
     * @param newLatitude 새로운 위도 (선택적)
     * @param newLongitude 새로운 경도 (선택적)
     * @param user 수정하는 사용자
     * @return 수정된 스냅샷
     */
    public Snapshot updateSnapshot(UUID snapshotId, String newContent, LocalDateTime newTimestamp, 
                                 String newLocationTag, Double newLatitude, Double newLongitude, User user) {
        try {
            // 1. 기존 스냅샷 조회 및 권한 확인
            Snapshot existingSnapshot = getSnapshotById(snapshotId, user);
            
            // 2. content 수정
            if (newContent != null && !newContent.trim().isEmpty()) {
                existingSnapshot.setContent(newContent.trim());
            }
            
            // 3. timestamp 수정
            if (newTimestamp != null) {
                existingSnapshot.setTimestamp(newTimestamp);
            }
            
            // 4. location 수정
            if (newLocationTag != null && !newLocationTag.trim().isEmpty()) {
                Location newLocation = findOrCreateLocation(newLocationTag.trim(), newLatitude, newLongitude, user);
                existingSnapshot.setLocation(newLocation);
            }
            
            // 5. 수정된 스냅샷 저장
            Snapshot updatedSnapshot = snapshotRepository.save(existingSnapshot);
            
            return updatedSnapshot;
            
        } catch (ResponseStatusException e) {
            throw e; // 이미 처리된 예외는 그대로 전달
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 스냅샷을 삭제합니다.
     *
     * @param snapshotId 삭제할 스냅샷 ID
     * @param user 삭제하는 사용자
     */
    public void deleteSnapshot(UUID snapshotId, User user) {
        try {
            // 1. 기존 스냅샷 조회 및 권한 확인
            Snapshot existingSnapshot = getSnapshotById(snapshotId, user);
            
            // 2. 스냅샷 삭제
            snapshotRepository.delete(existingSnapshot);
            
            // 3. 해당 DailySnapshot에 다른 스냅샷이 없다면 DailySnapshot도 삭제
            DailySnapshot dailySnapshot = existingSnapshot.getDailySnapshot();
            List<Snapshot> remainingSnapshots = snapshotRepository.findByDailySnapshotOrderByTimestampAsc(dailySnapshot);
            
            if (remainingSnapshots.isEmpty()) {
                dailySnapshotRepository.delete(dailySnapshot);
            }
            
        } catch (ResponseStatusException e) {
            throw e; // 이미 처리된 예외는 그대로 전달
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "스냅샷 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 위치 정보를 수정합니다.
     *
     * @param locationId 수정할 위치 ID
     * @param newTagName 새로운 태그명 (선택적)
     * @param newLatitude 새로운 위도 (선택적)
     * @param newLongitude 새로운 경도 (선택적)
     * @param user 수정하는 사용자
     * @return 수정된 위치
     */
    public Location updateLocation(Long locationId, String newTagName, Double newLatitude, Double newLongitude, User user) {
        try {
            // 1. 기존 위치 조회 및 권한 확인
            Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "위치를 찾을 수 없습니다: " + locationId));
            
            if (!existingLocation.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "해당 위치에 접근할 권한이 없습니다.");
            }
            
            // 2. 태그명 수정
            if (newTagName != null && !newTagName.trim().isEmpty()) {
                // 중복 태그명 확인
                Optional<Location> duplicateLocation = locationRepository.findByTagNameAndUser(newTagName.trim(), user);
                if (duplicateLocation.isPresent() && !duplicateLocation.get().getId().equals(locationId)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "이미 존재하는 위치 태그명입니다: " + newTagName);
                }
                existingLocation.setTagName(newTagName.trim());
            }
            
            // 3. GPS 좌표 수정
            if (newLatitude != null) {
                existingLocation.setLatitude(newLatitude);
            }
            if (newLongitude != null) {
                existingLocation.setLongitude(newLongitude);
            }
            
            // 4. 수정된 위치 저장
            Location updatedLocation = locationRepository.save(existingLocation);
            
            return updatedLocation;
            
        } catch (ResponseStatusException e) {
            throw e; // 이미 처리된 예외는 그대로 전달
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 위치를 삭제합니다.
     * 해당 위치를 사용하는 스냅샷들의 location은 null로 설정됩니다.
     *
     * @param locationId 삭제할 위치 ID
     * @param user 삭제하는 사용자
     */
    public void deleteLocation(Long locationId, User user) {
        try {
            // 1. 기존 위치 조회 및 권한 확인
            Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "위치를 찾을 수 없습니다: " + locationId));
            
            if (!existingLocation.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "해당 위치에 접근할 권한이 없습니다.");
            }
            
            // 2. 해당 위치를 사용하는 스냅샷들의 location을 null로 설정
            List<Snapshot> snapshotsWithLocation = snapshotRepository.findByLocationOrderByTimestampDesc(existingLocation);
            for (Snapshot snapshot : snapshotsWithLocation) {
                snapshot.setLocation(null);
                snapshotRepository.save(snapshot);
            }
            
            // 3. 위치 삭제
            locationRepository.delete(existingLocation);
            
        } catch (ResponseStatusException e) {
            throw e; // 이미 처리된 예외는 그대로 전달
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "위치 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
