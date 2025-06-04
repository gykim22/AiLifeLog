package com.pnu.ailifelogv2.controller;

import com.pnu.ailifelogv2.dto.User.ResUserDto;
import com.pnu.ailifelogv2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
public class UserController {
    private final AuthService authService;

    @GetMapping("/self")
    public ResponseEntity<ResUserDto> getCurrentUser(Authentication authentication) {
        ResUserDto userDto =  authService.getCurrentUser(authentication);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/self")
    public ResponseEntity<Void> deleteCurrentUser(String password, Authentication authentication) {
        authService.deleteCurrentUser(password,    authentication);
        return ResponseEntity.ok().build();
    }
}
