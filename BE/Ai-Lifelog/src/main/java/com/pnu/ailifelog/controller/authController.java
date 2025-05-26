package com.pnu.ailifelog.controller;

import com.pnu.ailifelog.dto.auth.LoginDto;
import com.pnu.ailifelog.dto.auth.TokenResponseDto;
import com.pnu.ailifelog.dto.auth.UserDto;
import com.pnu.ailifelog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            TokenResponseDto tokenResponse = authService.login(loginDto);
            log.info("사용자 로그인 성공: {}", loginDto.getUsername());
            return ResponseEntity.ok(tokenResponse);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("로그인에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto createdUser = authService.signup(userDto);
            log.info("새 사용자 등록 성공: {}", userDto.getUsername());
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("회원가입에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("JWT 인증이 필요한 엔드포인트입니다.");
    }
}
