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
import java.util.Optional;


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

}
