package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.*;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import com.avatar.challenge.planner.challenge.dto.DailyResponse;
import com.avatar.challenge.planner.exception.BizException;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.dto.LoginUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private static final Challenge CHALLENGE = Challenge.of("푸시업 30일 챌린지", 30, LocalDate.now(), 1L);

    @Mock
    private ChallengeRepository repository;

    @Mock
    private DailyService dailyService;

    @InjectMocks @Spy
    ChallengeService service;

    LoginUser loginUser = mock(LoginUser.class);


    @Test
    void create() {
        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);

        ChallengeResponse challengeResponse = ChallengeResponse.of(CHALLENGE);
        ChallengeRequest request = new ChallengeRequest("푸시업 30일 챌린지", 30, LocalDate.now());
        Flux<Daily> dailyFlux = challengeToDailyList(challengeResponse);


        when(repository.save(any()))
                .thenReturn(Mono.just(request.toEntity(1L)));
        when(dailyService.createWithChallenge(anyLong(), any(), anyLong()))
                .thenReturn(dailyFlux.map(DailyResponse::of));

        Mono<Long> response = service.create(request, loginUser);

        StepVerifier.create(response)
                .expectNext(1L)
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

        Mono<ChallengeResponse> response = service.changeStatus(1L, ChallengeStatus.DROP.toString(), loginUser);

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

    @Test
    void findResponseByStatus() {
        when(repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(anyLong(), any()))
                .thenReturn(Flux.just(CHALLENGE));
        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());

        Flux<ChallengeResponse> response = service.findResponseByStatus(ChallengeStatus.ONGOING.getKey(), loginUser).log();

        StepVerifier.create(response)
                .assertNext(challengeResponse -> {
                    assertEquals(challengeResponse.getStatus(), ChallengeStatus.ONGOING.getKey());
                })
                .verifyComplete();
    }

    @Test
    void scheduled() {
        LocalDate startDate = LocalDate.now().minusDays(3);
        Challenge c1 = Challenge.of("pushup 30일 100개 하기", 3, startDate, 1L);
        Challenge c2 = Challenge.of("스쿼트 30일 50개 하기", 3, startDate, 1L);
        Challenge c3 = Challenge.of("스쿼트 30일 50개 하기", 3, startDate, 2L);

        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 2L);
        ReflectionTestUtils.setField(c3, "id", 3L);

        assertFalse(c1.getSentNotification());
        assertFalse(c2.getSentNotification());

        when(repository.findAllByEndDate(any()))
                .thenReturn(Flux.just(c1, c2, c3));
        when(dailyService.findIncompleteByChallengeId(anyLong()))
                .thenReturn(Flux.empty());
        when(repository.save(c1))
                .thenReturn(Mono.just(c1));
        when(repository.save(c2))
                .thenReturn(Mono.just(c2));
        when(repository.save(c3))
                .thenReturn(Mono.just(c3));

        when(service.send(c1))
                .thenReturn(c1);
        when(service.send(c3))
                .thenReturn(c3);
        when(service.send(c2))
                .thenThrow(new BizException("error!!!"))
                                .thenReturn(c2);

        service.scheduled()
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();

        assertTrue(c1.getSentNotification());
        assertFalse(c2.getSentNotification());
        assertEquals(c1.getStatus(), ChallengeStatus.SUCCESS);
        assertEquals(c2.getStatus(), ChallengeStatus.SUCCESS);
    }
}
