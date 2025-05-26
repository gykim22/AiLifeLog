package com.pnu.ailifelog.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqLoginDto {
    @NotBlank(message = "로그인 ID을 입력하세요.")
    @Size(min=3, max = 15, message = "로그인 ID은 3자 이상 15자 이하로 입력하세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min=3, max = 100, message = "비밀번호는 3자 이상 15자 이하로 입력하세요.")
    private String password;
}
