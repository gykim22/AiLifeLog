package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.User.ReqAuthDto;
import com.pnu.ailifelogv2.dto.User.ResAuthDto;
import com.pnu.ailifelogv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {
    final private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResAuthDto> login(@RequestBody ReqAuthDto reqAuthDto) {
        // 로그인 로직 구현
        String token = authService.login(reqAuthDto.getUsername(), reqAuthDto.getPassword());
        return new ResponseEntity<>(new ResAuthDto(token), HttpStatus.OK); // 토큰을 포함한 응답 반환
    }

    @PostMapping("/signup")
    public ResponseEntity<ResAuthDto> signup(@RequestBody ReqAuthDto reqAuthDto) {
        // 회원가입 로직 구현
        String token = authService.signup(reqAuthDto.getUsername(), reqAuthDto.getPassword());
        return new ResponseEntity<>(new ResAuthDto(token), HttpStatus.CREATED); // 토큰을 포함한 응답 반환
    }
}
