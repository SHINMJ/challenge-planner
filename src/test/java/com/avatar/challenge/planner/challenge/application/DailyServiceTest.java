package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.Daily;
import com.avatar.challenge.planner.challenge.domain.DailyList;
import com.avatar.challenge.planner.challenge.domain.DailyRepository;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import com.avatar.challenge.planner.challenge.dto.ChallengeStatusEvent;
import com.avatar.challenge.planner.challenge.dto.DailyRequest;
import com.avatar.challenge.planner.challenge.dto.DailyResponse;
import com.avatar.challenge.planner.exception.BizException;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.dto.LoginUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class DailyServiceTest {
    private static final Challenge CHALLENGE = Challenge.of("푸시업 30일 챌린지", 30, LocalDate.now(), 1L);

    @Mock
    DailyRepository repository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    DailyService service;

    LoginUser loginUser = mock(LoginUser.class);

    @Test
    void createWithChallenge() {

        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);

        ChallengeResponse challengeResponse = ChallengeResponse.of(CHALLENGE);

        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(Flux.empty());
        when(repository.saveAll(anyIterable()))
                .thenReturn(challengeToDailyList(challengeResponse).log("dailylist"));

        Flux<DailyResponse> withChallenge = service.createWithChallenge(1L, CHALLENGE.getPeriod(), CHALLENGE.getOwnerId());

        StepVerifier.create(withChallenge)
                .expectNextCount(CHALLENGE.getPeriod())
                .verifyComplete();
    }

    @Test
    void createWithChallenge_fail_existsDailies() {

        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);

        ChallengeResponse challengeResponse = ChallengeResponse.of(CHALLENGE);

        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(challengeToDailyList(challengeResponse));

        Flux<DailyResponse> withChallenge = service.createWithChallenge(1L, CHALLENGE.getPeriod(), CHALLENGE.getOwnerId());

        StepVerifier.create(withChallenge)
                .expectErrorMatches(throwable -> throwable instanceof BizException)
                .verify();
    }

    @Test
    void findAllByChallengeId() {
        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);
        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());
        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(challengeToDailyList(ChallengeResponse.of(CHALLENGE)));

        Flux<DailyResponse> responseFlux = service.findAllByChallengeId(CHALLENGE.getId(), loginUser)
                .log("findAllByChallengeId");

        StepVerifier.create(responseFlux)
                .expectNextCount(CHALLENGE.getPeriod())
                .verifyComplete();
    }

    @Test
    void findAllByChallengeId_fail_unauthorized() {
        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);
        when(loginUser.getId())
                .thenReturn(2L);
        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(challengeToDailyList(ChallengeResponse.of(CHALLENGE)));

        Flux<DailyResponse> responseFlux = service.findAllByChallengeId(CHALLENGE.getId(), loginUser)
                .log("findAllByChallengeId");

        StepVerifier.create(responseFlux)
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException)
                .verify();
    }

    @Test
    void findCompletedByChallengeId_none() {
        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);

        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());
        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(challengeToDailyList(ChallengeResponse.of(CHALLENGE)));

        service.findIncompleteByChallengeId(CHALLENGE.getId(), loginUser)
                        .as(StepVerifier::create)
                                .expectNext(CHALLENGE.getPeriod().longValue())
                                        .verifyComplete();
    }

    @Test
    void findCompletedByChallengeId_allCompleted() {
        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);

        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());
        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(challengeToDailyList(ChallengeResponse.of(CHALLENGE))
                        .map(daily -> {
                            daily.completion();
                            return daily;
                        }));

        service.findIncompleteByChallengeId(CHALLENGE.getId(), loginUser)
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void update() {
        ReflectionTestUtils.setField(CHALLENGE, "id", 1L);
        Daily daily = Daily.of(CHALLENGE.getId(), 1, CHALLENGE.getOwnerId());

        when(loginUser.getId())
                .thenReturn(CHALLENGE.getOwnerId());
        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(daily));
        when(repository.save(any()))
                .thenReturn(Mono.just(daily));
        when(repository.findAllByChallengeId(anyLong()))
                .thenReturn(Flux.just(daily));

        service.update(1L, new DailyRequest(true, "comment"), loginUser)
                .as(StepVerifier::create)
                .verifyComplete();

        verify(eventPublisher)
                .publishEvent(any(ChallengeStatusEvent.class));

    }

    static Flux<Daily> challengeToDailyList(ChallengeResponse challengeResponse){
        DailyList dailyList = new DailyList();
        for (int i = 0; i < challengeResponse.getPeriod(); i++) {
            dailyList.add(Daily.of(challengeResponse.getId(), i+1, challengeResponse.getOwnerId()));
        }
        return Flux.just(dailyList.getDailies().toArray(new Daily[0]));
    }
}