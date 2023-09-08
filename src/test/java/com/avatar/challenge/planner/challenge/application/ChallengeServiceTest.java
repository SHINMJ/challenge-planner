package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.ChallengeRepository;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
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
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {
    @Mock
    private ChallengeRepository repository;

    @InjectMocks
    ChallengeService service;

    static Challenge CHALLENGE = Challenge.of("푸시업 30일 챌린지", 30, LocalDate.now(), 1L);

    @Test
    void create() {
        ChallengeRequest request = new ChallengeRequest("푸시업 30일 챌린지", 30, LocalDate.now());

        when(repository.save(any()))
                .thenReturn(Mono.just(request.toEntity(1L)));

        Mono<ChallengeResponse> response = service.create(request);

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

        Mono<ChallengeResponse> response = service.findResponseById(1L);

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
    void findAllByOwnerId() {

        when(repository.findAllByOwnerIdOrderByStartDateDesc(anyLong()))
                .thenReturn(Flux.just(CHALLENGE));

        Flux<ChallengeResponse> response = service.findResponseByOwnerId(1L);

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

        Mono<ChallengeResponse> response = service.changeStatus(1L, ChallengeStatus.DROP.toString());

        StepVerifier.create(response)
                .consumeNextWith(result -> assertEquals(result.getStatus(), ChallengeStatus.DROP.toString()))
                .verifyComplete();
    }

    @Test
    void findOngoingByOwnerId() {
        when(repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any()))
                .thenReturn(Flux.just(CHALLENGE));

        Flux<ChallengeResponse> response = service.findOngoingByOwnerId(1L);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }
}
