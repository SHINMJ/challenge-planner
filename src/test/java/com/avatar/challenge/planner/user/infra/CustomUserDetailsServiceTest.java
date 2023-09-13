package com.avatar.challenge.planner.user.infra;

import com.avatar.challenge.planner.exception.UserNotFoundException;
import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    UserRepository repository;

    @InjectMocks
    CustomUserDetailsService service;

    @Test
    void findByUsername_userNotFound() {
        when(repository.findByEmail(anyString()))
                .thenReturn(Mono.empty());

        service.findByUsername("test")
                .as(StepVerifier::create)
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void findByUsername() {

        when(repository.findByEmail(anyString()))
                .thenReturn(Mono.just(User.of("test@email.com", "1111", "test")));

        service.findByUsername("test@email.com")
                .as(StepVerifier::create)
                .consumeNextWith(result -> assertEquals(result.getUsername(), "test@email.com"))
                .verifyComplete();
    }
}