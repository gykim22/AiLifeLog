package com.pnu.ailifelog.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateUserDto {
    private String name;
    private String password;
}
