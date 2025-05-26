package com.pnu.ailifelog.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqSignupDto {
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 3, max = 50, message = "아이디는 3자 이상 50자 이하로 입력하세요.")
    private String loginId;

    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 3, max = 50, message = "닉네임은 3자 이상 50자 이하로 입력하세요.")
    private String nickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 3, max = 100, message = "비밀번호는 3자 이상 100자 이하로 입력하세요.")
    private String password;

}