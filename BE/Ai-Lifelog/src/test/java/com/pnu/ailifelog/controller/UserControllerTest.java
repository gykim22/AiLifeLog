package com.pnu.ailifelog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnu.ailifelog.config.TestConfig;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class UserControllerTest {

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
    @DisplayName("현재 사용자 정보 조회 성공")
    void getCurrentUser_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/me")
                        .with(authentication(testConfig.generateTestAuthentication(testUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("testuser"));
    }

    @Test
    @DisplayName("인증 없이 접근 시 실패")
    void accessWithoutAuth_Fail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }
} 