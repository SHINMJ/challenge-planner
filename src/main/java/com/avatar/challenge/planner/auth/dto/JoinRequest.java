package com.avatar.challenge.planner.auth.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {
    private String email;
    private String password;
    private String nickname;
}
