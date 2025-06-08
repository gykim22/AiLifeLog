package com.pnu.ailifelog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pnu.ailifelog.component.jwt.JwtProvider;
import com.pnu.ailifelog.entity.Role;
import com.pnu.ailifelog.entity.RoleName;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.RoleRepository;
import com.pnu.ailifelog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // JavaTimeModule 등록
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // LocalDate를 "yyyy-MM-dd" 형식으로 직렬화
        javaTimeModule.addSerializer(java.time.LocalDate.class, 
            new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // LocalDateTime을 "yyyy-MM-dd'T'HH:mm:ss" 형식으로 직렬화
        javaTimeModule.addSerializer(java.time.LocalDateTime.class, 
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        
        mapper.registerModule(javaTimeModule);
        
        // 배열 형태로 날짜를 직렬화하지 않도록 설정
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }

    public User createTestUser() {
        // Role 생성 또는 조회
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_USER)));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .name("테스트 사용자")
                .loginId("testuser")
                .password(passwordEncoder.encode("password123"))
                .roles(roles)
                .build();
        
        return userRepository.save(user);
    }

    public User createTestUser(String email, String loginId) {
        // Role 생성 또는 조회
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_USER)));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .name("테스트 사용자 " + loginId)
                .loginId(loginId)
                .password(passwordEncoder.encode("password123"))
                .roles(roles)
                .build();
        
        return userRepository.save(user);
    }

    public String generateTestToken(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getLoginId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return jwtProvider.generateToken(authentication);
    }

    public Authentication generateTestAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 