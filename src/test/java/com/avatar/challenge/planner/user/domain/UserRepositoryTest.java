package com.avatar.challenge.planner.user.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll()
                .subscribe().dispose();
    }

    @Test
    void create() {
        User testuser = User.of("test@email.com", "1111", "testuser");


        Flux<User> flux = repository.save(testuser)
                .thenMany(repository.findAll());

        StepVerifier.create(flux)
                .expectNext(testuser)
                .verifyComplete();
    }

    @Test
    void findByEmail() {
        User testuser = User.of("test@email.com", "1111", "testuser");
        repository.saveAll(Arrays.asList(testuser))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.findByEmail(testuser.getEmail())
                .as(StepVerifier::create)
                .consumeNextWith(result -> {
                    assertEquals(result.getNickname(), testuser.getNickname());
                })
                .verifyComplete();
    }

    @Test
    void findByEmail_notFound() {
        User testuser = User.of("test@email.com", "1111", "testuser");
        repository.saveAll(Arrays.asList(testuser))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.findByEmail("test1@email.com")
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByRefreshToken() {
        User testUser = User.of("test@email.com", "1111", "testUser");
        testUser.updateRefreshToken("refreshToken");
        repository.saveAll(Arrays.asList(testUser))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.findByRefreshToken("refreshToken")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

}