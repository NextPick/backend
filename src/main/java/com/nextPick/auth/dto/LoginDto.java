package com.nextPick.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginDto {
    private String username;
    private String password;
    private String fcmtoken;

}
