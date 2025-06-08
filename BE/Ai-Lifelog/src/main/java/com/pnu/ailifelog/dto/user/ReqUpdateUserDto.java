package com.pnu.ailifelog.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateUserDto {
    @Size(min = 3, max = 40, message = "닉네임은 3자 이상 40자 이하로 입력하세요.")
    private String name;
    
    @Size(min = 3, max = 100, message = "비밀번호는 3자 이상 100자 이하로 입력하세요.")
    private String password;
}
