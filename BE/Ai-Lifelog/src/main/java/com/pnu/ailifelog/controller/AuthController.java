package com.pnu.ailifelog.controller;

import com.pnu.ailifelog.dto.auth.ReqLoginDto;
import com.pnu.ailifelog.dto.auth.ResSignupDto;
import com.pnu.ailifelog.dto.auth.ResTokenDto;
import com.pnu.ailifelog.dto.auth.ReqSignupDto;
import com.pnu.ailifelog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResTokenDto> login(@Valid @RequestBody ReqLoginDto reqLoginDto) {
        try {
            ResTokenDto tokenResponse = authService.login(reqLoginDto);
            log.info("사용자 로그인 성공: {}", reqLoginDto.getLoginId());
            return ResponseEntity.ok(tokenResponse);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ResSignupDto> signup(@Valid @RequestBody ReqSignupDto reqSignupDto) {
        try {
            ResSignupDto createdUser = authService.signup(reqSignupDto);
            log.info("새 사용자 등록 성공: {}", reqSignupDto.getLoginId());
            return ResponseEntity.ok(createdUser);

        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("JWT 인증이 필요한 엔드포인트입니다.");
    }
}
