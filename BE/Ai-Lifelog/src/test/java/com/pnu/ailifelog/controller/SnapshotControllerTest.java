package com.pnu.ailifelog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnu.ailifelog.config.TestConfig;
import com.pnu.ailifelog.dto.snapshot.ReqCreateDto;
import com.pnu.ailifelog.dto.snapshot.ReqCreateWithTimeDto;
import com.pnu.ailifelog.dto.snapshot.ReqDateRangeDto;
import com.pnu.ailifelog.dto.snapshot.ReqUpdateSnapshotDto;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class SnapshotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = testConfig.createTestUser();
        testToken = testConfig.generateTestToken(testUser);
    }

    @Test
    @DisplayName("현재 시간 스냅샷 생성 성공")
    void createSnapshot_Success() throws Exception {
        // Given
        ReqCreateDto createDto = new ReqCreateDto("오늘 점심은 맛있었다", "회사", 37.5665, 126.9780);

        // When & Then
        mockMvc.perform(post("/api/v1/snapshots")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("오늘 점심은 맛있었다"))
                .andExpect(jsonPath("$.location.tagName").value("회사"));
    }

    @Test
    @DisplayName("특정 시간 스냅샷 생성 성공")
    void createSnapshotWithTime_Success() throws Exception {
        // Given
        ReqCreateWithTimeDto createDto = new ReqCreateWithTimeDto();
        createDto.setContent("어제 저녁 식사");
        createDto.setLocationTag("집");
        createDto.setTimestamp(LocalDateTime.now().minusDays(1));

        // When & Then
        mockMvc.perform(post("/api/v1/snapshots/with-time")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("어제 저녁 식사"))
                .andExpect(jsonPath("$.location.tagName").value("집"));
    }

    @Test
    @DisplayName("스냅샷 생성 실패 - 빈 내용")
    void createSnapshot_Fail_EmptyContent() throws Exception {
        // Given
        ReqCreateDto createDto = new ReqCreateDto("", "회사"); // 빈 내용

        // When & Then
        mockMvc.perform(post("/api/v1/snapshots")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("모든 스냅샷 조회 성공")
    void getAllSnapshots_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/snapshots")
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("날짜별 스냅샷 조회 성공")
    void getSnapshotsByDate_Success() throws Exception {
        // Given
        LocalDate today = LocalDate.now();

        // When & Then
        mockMvc.perform(get("/api/v1/snapshots/date/{date}", today)
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("기간별 스냅샷 조회 성공")
    void getSnapshotsByDateRange_Success() throws Exception {
        // Given
        ReqDateRangeDto rangeDto = new ReqDateRangeDto();
        rangeDto.setStartDate(LocalDate.now().minusDays(7));
        rangeDto.setEndDate(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/v1/snapshots/range")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rangeDto))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("위치별 스냅샷 조회 성공")
    void getSnapshotsByLocation_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/snapshots/location/{tag}", "회사")
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("일별 스냅샷 조회 성공")
    void getDailySnapshots_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/snapshots/daily")
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("위치 목록 조회 성공")
    void getLocations_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/snapshots/locations")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("인증 없이 접근 시 실패")
    void accessWithoutAuth_Fail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/snapshots"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 토큰으로 접근 시 실패")
    void accessWithInvalidToken_Fail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/snapshots")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
} 