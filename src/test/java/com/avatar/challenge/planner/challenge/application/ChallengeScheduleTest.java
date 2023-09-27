package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.ChallengeRepository;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.exception.BizException;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {"schedules.cron.notification=0/2 * * * * ?"}
)
public class ChallengeScheduleTest {

    @MockBean
    private ChallengeRepository repository;

    @MockBean
    private DailyService dailyService;

    @SpyBean
    private ChallengeScheduler scheduler;

    @Value("${schedules.cron.notification}")
    private String schedulesCron;

    @Test
    void scheduled() {
        assertTrue(CronExpression.isValidExpression(schedulesCron));

        await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(scheduler, atLeastOnce()).scheduled();
                });
    }


    @Test
    void scheduledMockTest() {
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

        when(scheduler.send(c1))
                .thenReturn(c1);
        when(scheduler.send(c3))
                .thenReturn(c3);
        when(scheduler.send(c2))
                .thenThrow(new BizException("error!!!"))
                                .thenReturn(c2);

        scheduler.dailyJobSaved()
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();

        assertTrue(c1.getSentNotification());
        assertFalse(c2.getSentNotification());
        assertEquals(c1.getStatus(), ChallengeStatus.SUCCESS);
        assertEquals(c2.getStatus(), ChallengeStatus.SUCCESS);
    }
}
