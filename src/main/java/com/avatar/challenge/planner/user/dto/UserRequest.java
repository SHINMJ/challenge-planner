package com.avatar.challenge.planner.user.dto;

import com.avatar.challenge.planner.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Getter
@RequiredArgsConstructor
public class UserRequest {
    private final String email;
    private final String password;
    private final String nickname;

    public Mono<User> toEntity(){
        return Mono.fromCallable(() -> User.of(email, password, nickname));
    }
}
