package com.avatar.challenge.planner.user.application;

import com.avatar.challenge.planner.exception.BizException;
import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.domain.UserRepository;
import com.avatar.challenge.planner.user.dto.UserRequest;
import com.avatar.challenge.planner.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService service;

    @Test
    void create() {
        User testUser = User.of("test@email.com", "1111", "testUser");
        when(repository.findByEmail(any()))
                .thenReturn(Mono.empty());
        when(repository.save(any()))
                .thenReturn(Mono.just(testUser));
        when(passwordEncoder.encode(any()))
                .thenReturn(testUser.getPassword());

        UserRequest request = new UserRequest("test@email.com", "1111", "testUser");

        Mono<UserResponse> userResponseMono = service.create(request);

        StepVerifier.create(userResponseMono)
                .consumeNextWith(userResponse -> {
                    assertAll(
                            () -> assertEquals(testUser.getEmail(), userResponse.getEmail()),
                            () -> assertEquals(testUser.getNickname(), userResponse.getNickname())
                    );
                })
                .verifyComplete();
    }

    @Test
    void create_fail_existsEmail() {
        User testUser = User.of("test@email.com", "1111", "testUser");
        when(repository.findByEmail(any()))
                .thenReturn(Mono.just(testUser));

        UserRequest request = new UserRequest("test@email.com", "1111", "testUser");

        Mono<UserResponse> userResponseMono = service.create(request);

        StepVerifier.create(userResponseMono)
                .expectErrorMatches(throwable -> throwable instanceof BizException && throwable.getMessage().equals("이미 사용중인 이메일입니다."))
                .verify();
    }
}