package com.avatar.challenge.planner.challenge.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChallengeRepository extends ReactiveCrudRepository<Challenge, Long> {
}
