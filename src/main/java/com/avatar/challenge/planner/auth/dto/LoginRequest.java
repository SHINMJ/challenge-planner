package com.avatar.challenge.planner.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    private String email;
    private String password;

    public boolean isEmpty(){
        return !(StringUtils.hasText(email) && StringUtils.hasText(password));
    }
}
