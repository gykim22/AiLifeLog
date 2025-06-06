package com.pnu.ailifelogv2.service;

import com.pnu.ailifelogv2.component.jwt.TokenProvider;
import com.pnu.ailifelogv2.dto.User.ResUserDto;
import com.pnu.ailifelogv2.entity.User;
import com.pnu.ailifelogv2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * 사용자 로그인 처리
     *
     * @param username 사용자의 이름
     * @param password 사용자의 비밀번호
     * @return JWT 토큰
     */
    public String login(String username, String password) {
        // 사용자 조회
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.");
        }
        log.info("비밀번호 검증 성공: username = {}", username);
        // JWT 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, user.getAuthorities()
        );
        return tokenProvider.generateToken(authentication);
    }

    /**
     * 사용자 회원가입 처리
     *
     * @param username 사용자의 이름
     * @param password 사용자의 비밀번호
     * @return JWT 토큰
     */
    public String signup(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 사용자 이름입니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        // 사용자 저장
        User newUser = userRepository.save(new User(username, encodedPassword));
        log.info("새 사용자 등록 성공: username = {}", username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                newUser.getUsername(), null, newUser.getAuthorities()
        );
        return tokenProvider.generateToken(authentication);
    }

    /**
     * 현재 인증된 사용자 정보 조회
     *
     * @param authentication 인증 정보
     * @return 현재 사용자 정보 DTO
     */
    public ResUserDto getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + currentUsername));
        return new ResUserDto(user.getId(), user.getUsername());
    }

    /**
     * 현재 인증된 사용자 삭제
     *
     * @param password 사용자의 비밀번호
     * @param authentication 인증 정보
     */
    public void deleteCurrentUser(String password, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + currentUsername));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }

        // 사용자 삭제
        userRepository.delete(user);
        log.info("사용자 삭제 성공: username = {}", currentUsername);
    }

}