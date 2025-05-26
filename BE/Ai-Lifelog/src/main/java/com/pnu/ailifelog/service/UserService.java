package com.pnu.ailifelog.service;

import com.pnu.ailifelog.dto.user.ResUserDto;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.RoleRepository;
import com.pnu.ailifelog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 이 클래스는 사용자 정보 조회, 수정, 삭제 등의 기능을 제공합니다.
 * * @author Swallow Lee
 */
@Service
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * 모든 사용자의 정보를 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 사용자 정보 리스트
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ResUserDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(ResUserDto::fromEntity).getContent();
    }
    public ResUserDto getUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + loginId)
        );
        return ResUserDto.fromEntity(user);
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 사용자 정보
     */
    public ResUserDto getUserById(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId);
        }
        return ResUserDto.fromEntity(userOptional.get());
    }

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param userId 수정할 사용자 ID
     * @param nickname 수정할 사용자 닉네임
     * @return 수정된 사용자 정보
     */
    public ResUserDto updateUser(UUID userId, String nickname, String password, @AuthenticationPrincipal User user) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId));
        if (
            !user.getId().equals(existingUser.getId()) &&
            !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        if (nickname != null && !nickname.isEmpty()) {
            existingUser.setName(nickname);
        }
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }
        User updatedUser = userRepository.save(existingUser);
        return ResUserDto.fromEntity(updatedUser);
    }

    /**
     * 사용자 정보를 삭제합니다.
     * @param userId 삭제할 사용자 ID
     */
    public void deleteUser(UUID userId, @AuthenticationPrincipal User user) {
        if (
                !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) &&
                !user.getId().equals(userId)
            ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId));
        userRepository.delete(existingUser);
    }
}
