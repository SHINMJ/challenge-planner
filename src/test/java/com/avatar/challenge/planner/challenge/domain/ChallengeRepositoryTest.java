package com.avatar.challenge.planner.challenge.domain;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest
public class ChallengeRepositoryTest {

    @Autowired
    ChallengeRepository challengeRepository;

    @BeforeEach
    void setUp() {
        challengeRepository.deleteAll()
                .thenMany(challengeRepository.findAll())
                .as(StepVerifier::create)
                .expectNextCount(0);
    }

    @Test
    void create() {
        LocalDate startDate = LocalDate.of(2023, 8, 31);
        Challenge challenge = Challenge.of("pushup 30일 100개 하기", 30, startDate, 1L);


        Flux<Challenge> flux = challengeRepository.save(challenge)
                .thenMany(challengeRepository.findAll());

        StepVerifier.create(flux)
                .expectNext(challenge)
                .verifyComplete();
    }

    @Test
    void update() {
        Challenge challenge = Challenge.of("pushup 30일 100개 하기", 30, LocalDate.now().plusDays(2), 1L);

        insertChallenge(challenge);

        challengeRepository.findById(challenge.getId())
                .map(c -> {
                    Challenge update = Challenge.of(c.getName(), 10, c.getStartDate(), 1L);
                    update.changeStatus(ChallengeStatus.ONGOING);
                    c.updateChallenge(update);
                    return c;
                })
                .flatMap(c -> challengeRepository.save(c))
                .then(challengeRepository.findById(challenge.getId()))
                .as(StepVerifier::create)
                .consumeNextWith(result -> {
                    assertAll(
                            () -> assertThat(result.getStatus()).isEqualTo(ChallengeStatus.ONGOING),
                            () -> assertThat(result.getPeriod()).isEqualTo(10)
                    );
                })
                .verifyComplete();
    }

    @Test
    void findAllByOwnerIdOrderByStartDateDesc() {
        Challenge c1 = Challenge.of("pushup 30일 100개 하기", 30, LocalDate.now().plusDays(2), 2L);
        Challenge c2 = Challenge.of("스쿼트 30일 50개 하기", 30, LocalDate.now().plusDays(6), 2L);
        insertChallenge(c1, c2);

        challengeRepository.findAllByOwnerIdOrderByStartDateDesc(2L)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAllByOwnerIdAndStatusOrderByStartDateDesc() {
        Challenge c1 = Challenge.of("pushup 30일 100개 하기", 30, LocalDate.now().minusDays(2), 3L);
        Challenge c2 = Challenge.of("스쿼트 30일 50개 하기", 30, LocalDate.now().plusDays(6), 3L);
        insertChallenge(c1, c2);

        challengeRepository.findAllByOwnerIdAndStatusOrderByStartDateDesc(3L, ChallengeStatus.ONGOING)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllByEndDate(){
        LocalDate now = LocalDate.now();
        Challenge c1 = Challenge.of("pushup 30일 100개 하기", 3, now, 1L);
        Challenge c2 = Challenge.of("스쿼트 30일 50개 하기", 5, now, 1L);

        insertChallenge(c1, c2);

        challengeRepository.findAllByEndDate(now.plusDays(3))
                .log()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();


    }

    private void insertChallenge(Challenge... challenge){
        challengeRepository.saveAll(Arrays.asList(challenge))
                .as(StepVerifier::create)
                .expectNextCount(Arrays.stream(challenge).count())
                .verifyComplete();
    }
}