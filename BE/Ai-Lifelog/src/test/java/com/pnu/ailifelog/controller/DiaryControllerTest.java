package com.pnu.ailifelog.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnu.ailifelog.config.TestConfig;
import com.pnu.ailifelog.dto.diary.ReqCreateDiaryDto;
import com.pnu.ailifelog.dto.diary.ReqUpdateDiaryDto;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.UserRepository;
import com.pnu.ailifelog.repository.DiaryRepository;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        diaryRepository.deleteAll();
        userRepository.deleteAll();
        testUser = testConfig.createTestUser();
    }

    @Test
    @DisplayName("일기 생성 성공")
    void createDiary_Success() throws Exception {
        // Given
        ReqCreateDiaryDto createDto = new ReqCreateDiaryDto();
        createDto.setTitle("오늘의 일기");
        createDto.setContent("오늘은 정말 좋은 하루였다. 새로운 프로젝트를 시작했고...");
        createDto.setDate(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("오늘의 일기"))
                .andExpect(jsonPath("$.content").value("오늘은 정말 좋은 하루였다. 새로운 프로젝트를 시작했고..."))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()));
    }

    @Test
    @DisplayName("일기 생성 실패 - 빈 제목")
    void createDiary_Fail_EmptyTitle() throws Exception {
        // Given
        ReqCreateDiaryDto createDto = new ReqCreateDiaryDto();
        createDto.setTitle(""); // 빈 제목
        createDto.setContent("내용은 있음");
        createDto.setDate(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("일기 생성 실패 - 중복 날짜")
    void createDiary_Fail_DuplicateDate() throws Exception {
        // Given - 첫 번째 일기 생성
        ReqCreateDiaryDto firstDto = new ReqCreateDiaryDto();
        firstDto.setTitle("첫 번째 일기");
        firstDto.setContent("첫 번째 내용");
        firstDto.setDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isCreated());

        // Given - 같은 날짜로 두 번째 일기 생성 시도
        ReqCreateDiaryDto secondDto = new ReqCreateDiaryDto();
        secondDto.setTitle("두 번째 일기");
        secondDto.setContent("두 번째 내용");
        secondDto.setDate(LocalDate.now()); // 같은 날짜

        // When & Then - CONFLICT 상태 코드 기대
        mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("모든 일기 조회 성공")
    void getAllDiaries_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("날짜별 일기 조회 성공 - 일기가 없는 경우")
    void getDiaryByDate_NotFound() throws Exception {
        // Given
        LocalDate today = LocalDate.now();

        // When & Then - 일기가 없으면 404 반환
        mockMvc.perform(get("/api/v1/diaries/date/{date}", today)
                        .with(authentication(testConfig.generateTestAuthentication(testUser))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("날짜별 일기 조회 성공 - 일기가 있는 경우")
    void getDiaryByDate_Success() throws Exception {
        // Given - 먼저 일기 생성
        ReqCreateDiaryDto createDto = new ReqCreateDiaryDto();
        createDto.setTitle("오늘의 일기");
        createDto.setContent("오늘의 내용");
        createDto.setDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/api/v1/diaries/date/{date}", LocalDate.now())
                        .with(authentication(testConfig.generateTestAuthentication(testUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("오늘의 일기"))
                .andExpect(jsonPath("$.content").value("오늘의 내용"));
    }

    @Test
    @DisplayName("키워드 검색 성공")
    void searchDiaries_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/diaries/search")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .param("keyword", "좋은")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("키워드 검색 실패 - 빈 키워드")
    void searchDiaries_Fail_EmptyKeyword() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/diaries/search")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .param("keyword", "")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("일기 수정 성공")
    void updateDiary_Success() throws Exception {
        // Given - 먼저 일기 생성
        ReqCreateDiaryDto createDto = new ReqCreateDiaryDto();
        createDto.setTitle("원본 제목");
        createDto.setContent("원본 내용");
        createDto.setDate(LocalDate.now());

        String response = mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // 생성된 일기의 ID 추출
        JsonNode jsonNode = objectMapper.readTree(response);
        UUID diaryId = UUID.fromString(jsonNode.get("id").asText());

        // Given - 수정 데이터
        ReqUpdateDiaryDto updateDto = new ReqUpdateDiaryDto();
        updateDto.setTitle("수정된 제목");
        updateDto.setContent("수정된 내용");

        // When & Then
        mockMvc.perform(put("/api/v1/diaries/{diaryId}", diaryId)
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("일기 삭제 성공")
    void deleteDiary_Success() throws Exception {
        // Given - 먼저 일기 생성
        ReqCreateDiaryDto createDto = new ReqCreateDiaryDto();
        createDto.setTitle("삭제할 일기");
        createDto.setContent("삭제할 내용");
        createDto.setDate(LocalDate.now());

        String response = mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(testUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // 생성된 일기의 ID 추출
        JsonNode jsonNode = objectMapper.readTree(response);
        UUID diaryId = UUID.fromString(jsonNode.get("id").asText());

        // When & Then - 삭제는 204 No Content 반환
        mockMvc.perform(delete("/api/v1/diaries/{diaryId}", diaryId)
                        .with(authentication(testConfig.generateTestAuthentication(testUser))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("인증 없이 접근 시 실패")
    void accessWithoutAuth_Fail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/diaries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 토큰으로 접근 시 실패")
    void accessWithInvalidToken_Fail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/diaries")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 일기 조회 시 실패")
    void getDiary_NotFound() throws Exception {
        // When & Then - 랜덤 UUID 사용
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/diaries/{diaryId}", randomId)
                        .with(authentication(testConfig.generateTestAuthentication(testUser))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("다른 사용자의 일기 접근 시 실패")
    void accessOtherUserDiary_Fail() throws Exception {
        // Given - 다른 사용자 생성
        User otherUser = testConfig.createTestUser("other@test.com", "otheruser");
        
        // Given - 다른 사용자의 일기 생성
        ReqCreateDiaryDto createDto = new ReqCreateDiaryDto();
        createDto.setTitle("다른 사용자의 일기");
        createDto.setContent("다른 사용자의 내용");
        createDto.setDate(LocalDate.now().minusDays(1)); // 다른 날짜 사용

        String response = mockMvc.perform(post("/api/v1/diaries")
                        .with(authentication(testConfig.generateTestAuthentication(otherUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        UUID diaryId = UUID.fromString(jsonNode.get("id").asText());

        // When & Then - 다른 사용자로 접근 시도
        mockMvc.perform(get("/api/v1/diaries/{diaryId}", diaryId)
                        .with(authentication(testConfig.generateTestAuthentication(testUser))))
                .andExpect(status().isForbidden());
    }
} 