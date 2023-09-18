package com.avatar.challenge.planner.user.dto;

import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.infra.CustomUserDetails;
import lombok.ToString;

@ToString
public class LoginUser extends CustomUserDetails {
    private User user;

    public LoginUser(User user) {
        super(user);
        this.user = user;
    }

    public Long getId(){
        return this.user.getId();
    }

    public String nickname(){
        return this.user.getNickname();
    }
}
