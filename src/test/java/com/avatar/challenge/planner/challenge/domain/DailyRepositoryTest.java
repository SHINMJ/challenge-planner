package com.avatar.challenge.planner.challenge.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest
class DailyRepositoryTest {

    @Autowired
    DailyRepository repository;

    @Autowired
    ChallengeRepository challengeRepository;

    Challenge testChallenge;

    @BeforeEach
    void setUp() {
        testChallenge = Challenge.of("pushup 30일 100개 하기", 5, LocalDate.now(), 1L);

        repository.deleteAll().subscribe().dispose();
        challengeRepository.deleteAll().subscribe().dispose();
    }

    @Test
    void create() {

        saveChallenge(testChallenge)
                .flatMap(it -> {
                    Daily daily = Daily.of(it.getId(), 1, it.getOwnerId());
                    return repository.save(daily);
                })
                .as(StepVerifier::create)
                .consumeNextWith(daily -> {
                    assertAll(
                            () -> assertEquals(daily.getOwnerId(), testChallenge.getOwnerId()),
                            () -> assertFalse(daily.getCompletedAt())
                    );
                })
                .verifyComplete();
    }

    @Test
    void creates() {
        saveDailies()
                .log()
                .as(StepVerifier::create)
                .expectNextCount(testChallenge.getPeriod())
                .verifyComplete();
    }

    @Test
    void findAllByChallengeId() {
        saveDailies().last()
                .flatMapMany(daily -> repository.findAllByChallengeId(daily.getChallengeId()))
                .log("findAllByChallengeId")
                .as(StepVerifier::create)
                .expectNextCount(testChallenge.getPeriod())
                .verifyComplete();

    }

    private Mono<Challenge> saveChallenge(Challenge challenge){
        return challengeRepository.save(challenge);
    }

    private Flux<Daily> saveDailies(){
        return saveChallenge(testChallenge)
                .flatMapMany(challenge -> {
                    Integer period = challenge.getPeriod();
                    DailyList dailyList = new DailyList();
                    for (int i = 0; i < period; i++) {
                        dailyList.add(Daily.of(challenge.getId(), i+1, challenge.getOwnerId()));
                    }
                    return repository.saveAll(dailyList.getDailies());
                });

    }

}