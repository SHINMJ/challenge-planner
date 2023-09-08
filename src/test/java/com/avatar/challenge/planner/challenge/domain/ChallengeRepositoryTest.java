package com.avatar.challenge.planner.challenge.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

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
        Challenge challenge = Challenge.of("pushup 30일 100개 하기", 30, startDate);


        Flux<Challenge> flux = challengeRepository.save(challenge)
                .thenMany(challengeRepository.findAll());

        StepVerifier.create(flux)
                .expectNext(challenge)
                .verifyComplete();
    }

    @Test
    void update() {
        LocalDate startDate = LocalDate.of(2023, 8, 31);
        Challenge challenge = Challenge.of("pushup 30일 100개 하기", 30, startDate);

        insertChallenge(challenge);

        challengeRepository.findById(challenge.getId())
                .map(c -> {
                    Challenge update = Challenge.of(c.getName(), 10, c.getStartDate());
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

    private void insertChallenge(Challenge challenge){
        challengeRepository.save(challenge)
                .as(StepVerifier::create)
                .expectNextCount(1L)
                .verifyComplete();
    }
}