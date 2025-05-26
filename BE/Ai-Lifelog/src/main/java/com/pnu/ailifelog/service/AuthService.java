package com.pnu.ailifelog.service;

import com.pnu.ailifelog.component.jwt.JwtProvider;
import com.pnu.ailifelog.dto.auth.ReqLoginDto;
import com.pnu.ailifelog.dto.auth.ResSignupDto;
import com.pnu.ailifelog.dto.auth.ResTokenDto;
import com.pnu.ailifelog.dto.auth.ReqSignupDto;
import com.pnu.ailifelog.entity.Role;
import com.pnu.ailifelog.entity.RoleName;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.RoleRepository;
import com.pnu.ailifelog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public ResTokenDto login(ReqLoginDto reqLoginDto) {
        log.info("로그인 시도: username = {}", reqLoginDto.getLoginId());
        
        // 사용자 조회
        Optional<User> userOptional = userRepository.findByUsername(reqLoginDto.getLoginId());
        log.info("사용자 조회 결과: {}", userOptional.isPresent() ? "찾음" : "없음");
        
        User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + reqLoginDto.getLoginId()));

        log.info("조회된 사용자: id={}, username={}, roles={}", 
                user.getId(), user.getLoginId(), user.getRoles().size());

        // 비밀번호 검증
        if (!passwordEncoder.matches(reqLoginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }

        log.info("비밀번호 검증 성공");

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getLoginId(),
                null,
                user.getAuthorities()
        );
        // JWT 토큰 생성
        String token = jwtProvider.generateToken(authentication);
        log.info("JWT 토큰 생성 완료");
        return new ResTokenDto(token);
    }

    @Transactional
    public ResSignupDto signup(ReqSignupDto reqSignupDto) {
        // 중복 사용자명 검증
        if (userRepository.existsByUsername(reqSignupDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + reqSignupDto.getLoginId());
        }

        // 기본 역할 조회 (USER 역할)
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("기본 역할을 찾을 수 없습니다."));

        // 새 사용자 생성
        User user = new User(
                reqSignupDto.getNickname(),
                reqSignupDto.getLoginId(),
                passwordEncoder.encode(reqSignupDto.getPassword())
        );
        user.setRoles(Set.of(userRole));

        // 사용자 저장
        User savedUser = userRepository.save(user);
        return new ResSignupDto(savedUser);
    }
} 