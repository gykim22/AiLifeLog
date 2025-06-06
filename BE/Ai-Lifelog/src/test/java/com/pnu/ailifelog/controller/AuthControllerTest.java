package com.pnu.ailifelog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnu.ailifelog.config.TestConfig;
import com.pnu.ailifelog.dto.auth.ReqLoginDto;
import com.pnu.ailifelog.dto.auth.ReqSignupDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = testConfig.createTestUser();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() throws Exception {
        // Given
        String signupJson = """
                {
                    "loginId": "newuser",
                    "nickname": "새로운 사용자",
                    "password": "password123"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("newuser"))
                .andExpect(jsonPath("$.nickname").value("새로운 사용자"));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 로그인 ID")
    void signup_Fail_DuplicateLoginId() throws Exception {
        // Given
        String signupJson = """
                {
                    "loginId": "testuser",
                    "nickname": "새로운 사용자",
                    "password": "password123"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검증 오류")
    void signup_Fail_ValidationError() throws Exception {
        // Given
        ReqSignupDto signupDto = new ReqSignupDto();
        signupDto.setLoginId("ab"); // 너무 짧은 ID
        signupDto.setNickname("");      // 빈 이름
        signupDto.setPassword("12"); // 너무 짧은 비밀번호

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // Given
        String loginJson = """
                {
                    "loginId": "testuser",
                    "password": "password123"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() throws Exception {
        // Given
        ReqLoginDto loginDto = new ReqLoginDto();
        loginDto.setLoginId("testuser");
        loginDto.setPassword("wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Fail_UserNotFound() throws Exception {
        // Given
        ReqLoginDto loginDto = new ReqLoginDto();
        loginDto.setLoginId("nonexistent");
        loginDto.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패 - 유효성 검증 오류")
    void login_Fail_ValidationError() throws Exception {
        // Given
        ReqLoginDto loginDto = new ReqLoginDto();
        loginDto.setLoginId(""); // 빈 로그인 ID
        loginDto.setPassword(""); // 빈 비밀번호

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("테스트 엔드포인트 - 인증 필요")
    void test_RequiresAuth() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/auth/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("테스트 엔드포인트 - 인증 성공")
    void test_WithAuth_Success() throws Exception {
        // Given
        String token = testConfig.generateTestToken(testUser);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("JWT 인증이 필요한 엔드포인트입니다."));
    }
} 