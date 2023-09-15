package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.ChallengeRepository;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.dto.LoginUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {
    private static final Challenge CHALLENGE = Challenge.of("푸시업 30일 챌린지", 30, LocalDate.now(), 1L);

    @Mock
    private ChallengeRepository repository;

    @InjectMocks
    ChallengeService service;

    LoginUser loginUser = mock(LoginUser.class);


    @Test
    void create() {
        ChallengeRequest request = new ChallengeRequest("푸시업 30일 챌린지", 30, LocalDate.now());

        when(repository.save(any()))
                .thenReturn(Mono.just(request.toEntity(1L)));

        Mono<ChallengeResponse> response = service.create(request, loginUser);

        StepVerifier.create(response)
                .consumeNextWith(result -> {
                    assertAll(
                            () -> assertEquals(result.getName(), request.getName()),
                            () -> assertEquals(result.getStatus(), ChallengeStatus.ONGOING.toString())
                    );
                })
                .verifyComplete();
    }

    @Test
    void findById() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(CHALLENGE));
        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());

        Mono<ChallengeResponse> response = service.findResponseById(1L, loginUser);

        StepVerifier.create(response)
                .consumeNextWith(result -> {
                    assertAll(
                            () -> assertEquals(result.getName(), CHALLENGE.getName()),
                            () -> assertEquals(result.getStatus(), ChallengeStatus.ONGOING.toString())
                    );
                })
                .verifyComplete();

    }

    @Test
    void findById_fail_unauthorized() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(CHALLENGE));
        when(loginUser.getId())
                .thenReturn(2L);

        Mono<ChallengeResponse> response = service.findResponseById(1L, loginUser);

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof UnauthorizedException && throwable.getMessage().equals("권한이 없습니다."))
                .verify();

    }

    @Test
    void findAllByOwnerId() {

        when(repository.findAllByOwnerIdOrderByStartDateDesc(anyLong()))
                .thenReturn(Flux.just(CHALLENGE));
        when(loginUser.getId())
                .thenReturn(1L);

        Flux<ChallengeResponse> response = service.findResponseByOwnerId(loginUser);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void changeStatus() {
        Mono<Challenge> challengeMono = Mono.just(CHALLENGE);
        when(repository.findById(anyLong()))
                .thenReturn(challengeMono);
        when(repository.save(any()))
                .thenReturn(challengeMono);
        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());

        Mono<ChallengeResponse> response = service.changeStatus(loginUser, 1L, ChallengeStatus.DROP.toString());

        StepVerifier.create(response)
                .consumeNextWith(result -> assertEquals(result.getStatus(), ChallengeStatus.DROP.toString()))
                .verifyComplete();
    }

    @Test
    void findOngoingByOwnerId() {
        when(repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any()))
                .thenReturn(Flux.just(CHALLENGE));
        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());

        Flux<ChallengeResponse> response = service.findOngoingByOwnerId(loginUser);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }
}
