package com.avatar.challenge.planner.user.dto;

import com.avatar.challenge.planner.user.domain.User;
import reactor.core.publisher.Mono;

public record UserRequest(String email, String password, String nickname) {
    public Mono<User> toEntity(){
        return Mono.fromCallable(() -> User.of(this.email, this.password, this.nickname));
    }
}
