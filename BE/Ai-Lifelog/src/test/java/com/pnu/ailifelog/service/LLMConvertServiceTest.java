package com.pnu.ailifelog.service;

import com.pnu.ailifelog.entity.*;
import com.pnu.ailifelog.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LLMConvertServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DailySnapshotRepository dailySnapshotRepository;
    @Autowired
    private SnapshotRepository snapshotRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LLMConvertService llmConvertService;

    @Value("classpath:template/DailySnapshotSystemTemplate.txt")
    private Resource dailySnapshotSystemTemplateFile;
    @Value("classpath:template/DailySnapshotUserTemplate.txt")
    private Resource dailySnapshotUserTemplateFile;

    private User testUser;
    private List<Snapshot> testSnapshots;
    private DailySnapshot testDailySnapshot;
    private Diary testDiary;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        User testUser = setupTestUser();
        setupDailySnapshot();
    }

    private User setupTestUser() {
        // 1. Role 생성
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(RoleName.ROLE_USER);
                    return roleRepository.save(role);
                });

        // 2. 테스트 사용자 생성
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        testUser = User.builder()
                .name("테스트 사용자")
                .loginId("testuser")
                .password(passwordEncoder.encode("password123"))
                .roles(roles)
                .build();
        return userRepository.save(testUser);
    }

    private void setupDailySnapshot() {
        // 1. Role 생성
        // 3. DailySnapshot 생성
        testDailySnapshot = new DailySnapshot();
        testDailySnapshot.setDate(LocalDate.now());
        testDailySnapshot.setUser(testUser);
        testDailySnapshot = dailySnapshotRepository.save(testDailySnapshot);

        // 4. 위치 정보 생성
        Location homeLocation = Location.builder()
                .tagName("집")
                .latitude(37.5665)
                .longitude(126.9780)
                .user(testUser)
                .build();
        homeLocation = locationRepository.save(homeLocation);

        Location officeLocation = Location.builder()
                .tagName("회사")
                .latitude(37.5660)
                .longitude(126.9784)
                .user(testUser)
                .build();
        officeLocation = locationRepository.save(officeLocation);

        Location cafeLocation = Location.builder()
                .tagName("카페")
                .latitude(37.5670)
                .longitude(126.9790)
                .user(testUser)
                .build();
        cafeLocation = locationRepository.save(cafeLocation);

        // 5. 스냅샷들 생성
        testSnapshots = new ArrayList<>();

        // 아침 스냅샷
        Snapshot morningSnapshot = new Snapshot();
        morningSnapshot.setContent("아침에 일어나서 커피를 마시며 하루를 시작했다");
        morningSnapshot.setTimestamp(LocalDateTime.now().withHour(8).withMinute(0));
        morningSnapshot.setLocation(homeLocation);
        morningSnapshot.setDailySnapshot(testDailySnapshot);
        testSnapshots.add(snapshotRepository.save(morningSnapshot));

        // 출근 스냅샷
        Snapshot workSnapshot = new Snapshot();
        workSnapshot.setContent("회사에 도착해서 오늘 할 일을 정리했다");
        workSnapshot.setTimestamp(LocalDateTime.now().withHour(9).withMinute(30));
        workSnapshot.setLocation(officeLocation);
        workSnapshot.setDailySnapshot(testDailySnapshot);
        testSnapshots.add(snapshotRepository.save(workSnapshot));

        // 점심 스냅샷
        Snapshot lunchSnapshot = new Snapshot();
        lunchSnapshot.setContent("동료들과 함께 맛있는 점심을 먹었다");
        lunchSnapshot.setTimestamp(LocalDateTime.now().withHour(12).withMinute(30));
        lunchSnapshot.setLocation(cafeLocation);
        lunchSnapshot.setDailySnapshot(testDailySnapshot);
        testSnapshots.add(snapshotRepository.save(lunchSnapshot));

        // 오후 스냅샷
        Snapshot afternoonSnapshot = new Snapshot();
        afternoonSnapshot.setContent("프로젝트 회의를 진행하고 업무를 마무리했다");
        afternoonSnapshot.setTimestamp(LocalDateTime.now().withHour(15).withMinute(0));
        afternoonSnapshot.setLocation(officeLocation);
        afternoonSnapshot.setDailySnapshot(testDailySnapshot);
        testSnapshots.add(snapshotRepository.save(afternoonSnapshot));

        // 저녁 스냅샷
        Snapshot eveningSnapshot = new Snapshot();
        eveningSnapshot.setContent("집에 돌아와서 가족과 함께 저녁을 먹었다");
        eveningSnapshot.setTimestamp(LocalDateTime.now().withHour(19).withMinute(0));
        eveningSnapshot.setLocation(homeLocation);
        eveningSnapshot.setDailySnapshot(testDailySnapshot);
        testSnapshots.add(snapshotRepository.save(eveningSnapshot));

        // DailySnapshot에 스냅샷 리스트 설정
        testDailySnapshot.setSnapshots(testSnapshots);
        dailySnapshotRepository.save(testDailySnapshot);
    }

    private void setupDiary(){
        testDiary = new Diary();
        testDiary.setTitle("테스트 일기 제목");
        testDiary.setContent("오늘은 아침에 산책을 하고, 점심에는 친구를 만났으며, 저녁에는 집에서 책을 읽었다.");
        testDiary.setDate(LocalDate.now());
        testDiary.setUser(testUser);
        testDiary = diaryRepository.save(testDiary);
    }

    @Test
    @DisplayName("정상적인 DailySnapshot 요약 테스트 - 데이터 검증")
    void testToDiaryDataValidation() {
        // Given
        UUID dailySnapshotId = testDailySnapshot.getId();
        User owner = testUser;
        ChatResponse res = llmConvertService.toDiary(dailySnapshotId, owner);
        // 제대로 반환되었는지 검증
        assertNotNull(res);
        System.out.println(res.getResult());
    }


    @Test
    @DisplayName("정상적인 Diary 요약 테스트 - 데이터 검증")
    void testToDailySnapshotDataValidation() {
        // Given
        setupDiary();
        UUID diaryId = testDiary.getId();
        User owner = testUser;
        ChatResponse res = llmConvertService.toDailySnapShot(diaryId, owner);
        // 제대로 반환되었는지 검증
        assertNotNull(res);
        System.out.println(res.getResult());
    }
}