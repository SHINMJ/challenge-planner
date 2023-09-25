package com.avatar.challenge.planner.challenge.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface ChallengeRepository extends ReactiveCrudRepository<Challenge, Long> {
    Flux<Challenge> findAllByOwnerIdOrderByStartDateDesc(Long ownerId);
    Flux<Challenge> findAllByOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, ChallengeStatus status);
    Flux<Challenge> findAllByEndDate(LocalDate endDate);
}
