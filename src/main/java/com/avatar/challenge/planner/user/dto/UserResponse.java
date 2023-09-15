package com.avatar.challenge.planner.user.dto;


import com.avatar.challenge.planner.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;

    public static UserResponse of(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

}
