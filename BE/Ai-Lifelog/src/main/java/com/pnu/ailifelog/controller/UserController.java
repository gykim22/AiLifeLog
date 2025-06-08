package com.pnu.ailifelog.controller;

import com.pnu.ailifelog.dto.user.ReqUpdateUserDto;
import com.pnu.ailifelog.dto.user.ResUserDto;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.service.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 관련 API를 처리하는 컨트롤러입니다.
 * 이 컨트롤러는 사용자 정보 조회, 수정, 삭제 등의 기능을 제공합니다.
 *
 * @author Swallow Lee
 * @version 1.0
 */

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 모든 사용자의 정보를 페이지네이션하여 조회합니다.
     * 관리자 권한이 필요합니다.
     * @return 사용자 정보 리스트
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ResUserDto>> getAllUsers(@PageableDefault Pageable pageable) {
        List<ResUserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * 특정 사용자의 정보를 조회합니다.
     * @param loginId 사용자 로그인 ID
     * @return 사용자 정보
     */
    @GetMapping("/{loginId}")
    public ResponseEntity<ResUserDto> getUserByLoginId(@PathVariable String loginId) {
        ResUserDto user = userService.getUserByLoginId(loginId);
        return ResponseEntity.ok(user);
    }

    /**
     * 현재 인증된 사용자의 정보를 조회합니다.
     * @param user 인증된 사용자 정보(AuthenticationPrincipal 어노테이션 사용하여 주입)
     * @return 현재 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ResUserDto> getCurrentUser(@AuthenticationPrincipal User user) {
        ResUserDto currentUser = userService.getUserByLoginId(user.getLoginId());
        return ResponseEntity.ok(currentUser);
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     * @param id 사용자 식별자
     * @return 조회된 사용자 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResUserDto> getUserById(@PathVariable UUID id) {
        ResUserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 정보를 수정합니다.
     * @param id 수정할 사용자 ID
     * @param userDto 수정할 사용자 정보 DTO
     * @param principal 인증된 사용자 정보(AuthenticationPrincipal 어노테이션 사용하여 주입)
     * @return 수정된 사용자 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResUserDto> updateUser(
            @PathVariable UUID id,
            @RequestBody ReqUpdateUserDto userDto,
            @AuthenticationPrincipal User principal) {
        if (
                (userDto.getName() == null || userDto.getName().isEmpty()) &&
                (userDto.getPassword() == null || userDto.getPassword().isEmpty())
        ) {
            return ResponseEntity.badRequest().build();
        }
        ResUserDto updatedUser = userService.updateUser(id, userDto.getName(), userDto.getPassword(), principal);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 사용자 정보를 삭제합니다.
     * @param id 삭제할 사용자 ID
     * @return 삭제된 사용자 정보
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal User principal) {
        userService.deleteUser(id, principal);
        return ResponseEntity.noContent().build();
    }
}
