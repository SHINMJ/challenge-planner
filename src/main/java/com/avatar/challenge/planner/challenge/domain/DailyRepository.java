package com.avatar.challenge.planner.challenge.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DailyRepository extends ReactiveCrudRepository<Daily, Long> {
    Flux<Daily> findAllByChallengeId(Long challengeId);
}
