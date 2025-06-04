package com.pnu.ailifelogv2.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class ReqAuthDto {

    @NotNull @Size(min = 3, max = 50, message = "사용자 이름은 3자 이상 50자 이하이어야 합니다.")
    String username;

    @NotNull @Size(min = 3, max = 100, message = "비밀번호는 3자 이상 100자 이하이어야 합니다.")
    String password;
}
