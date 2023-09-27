package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.*;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import com.avatar.challenge.planner.challenge.dto.DailyResponse;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.dto.LoginUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static com.avatar.challenge.planner.challenge.application.DailyServiceTest.challengeToDailyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {
    private Challenge testChallenge;

    @Mock
    private ChallengeRepository repository;

    @Mock
    private DailyService dailyService;

    @InjectMocks @Spy
    ChallengeService service;

    LoginUser loginUser = mock(LoginUser.class);

    @BeforeEach
    void setUp() {
        testChallenge = Challenge.of("푸시업 30일 챌린지", 30, LocalDate.now(), 1L);
    }

    @Test
    void create() {
        ReflectionTestUtils.setField(testChallenge, "id", 1L);

        ChallengeRequest request = new ChallengeRequest(testChallenge.getName(), testChallenge.getPeriod(), testChallenge.getStartDate());
        ChallengeResponse challengeResponse = ChallengeResponse.of(testChallenge);
        Flux<Daily> dailyFlux = challengeToDailyList(challengeResponse);


        when(repository.save(any()))
                .thenReturn(Mono.just(testChallenge));
        when(dailyService.createWithChallenge(challengeResponse.getId(), challengeResponse.getPeriod(), challengeResponse.getOwnerId()))
                .thenReturn(dailyFlux.map(DailyResponse::of));

        Mono<Long> response = service.create(request, loginUser);

        StepVerifier.create(response)
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void findById() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(testChallenge));
        when(loginUser.getId())
                .thenReturn(testChallenge.getOwnerId());

        Mono<ChallengeResponse> response = service.findResponseById(1L, loginUser);

        StepVerifier.create(response)
                .consumeNextWith(result -> {
                    assertAll(
                            () -> assertEquals(result.getName(), testChallenge.getName()),
                            () -> assertEquals(result.getStatus(), ChallengeStatus.ONGOING.getKey())
                    );
                })
                .verifyComplete();

    }

    @Test
    void findById_fail_unauthorized() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(testChallenge));
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
                .thenReturn(Flux.just(testChallenge));
        when(loginUser.getId())
                .thenReturn(1L);

        Flux<ChallengeResponse> response = service.findResponseByOwnerId(loginUser);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void changeStatus() {
        ReflectionTestUtils.setField(testChallenge, "id", 1L);

        Mono<Challenge> challengeMono = Mono.just(testChallenge);
        when(repository.findById(anyLong()))
                .thenReturn(challengeMono);
        when(repository.save(any()))
                .thenReturn(challengeMono);
        when(loginUser.getId())
                .thenReturn(testChallenge.getOwnerId());

        Mono<ChallengeResponse> response = service.changeStatus(1L, ChallengeStatus.DROP.getKey(), loginUser);

        StepVerifier.create(response)
                .consumeNextWith(result -> assertEquals(result.getStatus(), ChallengeStatus.DROP.getKey()))
                .verifyComplete();
    }

    @Test
    void findOngoingByOwnerId() {
        when(repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any()))
                .thenReturn(Flux.just(testChallenge));
        when(loginUser.getId())
                .thenReturn(testChallenge.getOwnerId());

        Flux<ChallengeResponse> response = service.findOngoingByOwnerId(loginUser);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findResponseByStatus() {
        ReflectionTestUtils.setField(testChallenge, "id", 1L);

        when(repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any()))
                .thenReturn(Flux.just(testChallenge));
        when(loginUser.getId())
                .thenReturn(testChallenge.getOwnerId());

        Flux<ChallengeResponse> response = service.findResponseByStatus(ChallengeStatus.ONGOING.getKey(), loginUser).log();

        StepVerifier.create(response)
                .assertNext(challengeResponse -> {
                    assertEquals(challengeResponse.getStatus(), ChallengeStatus.ONGOING.getKey());
                })
                .verifyComplete();
    }

}
