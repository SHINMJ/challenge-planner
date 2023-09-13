package com.avatar.challenge.planner.auth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public static TokenResponse of(String accessToken, String refreshToken){
        return new TokenResponse(accessToken, refreshToken);
    }
}
