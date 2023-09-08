package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.ChallengeRepository;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Transactional
@RequiredArgsConstructor
@Service
public class ChallengeService {
    private final ChallengeRepository repository;

    public Mono<ChallengeResponse> create(ChallengeRequest request) {
        Long userId = 1L;
        return repository.save(request.toEntity(userId))
                .map(ChallengeResponse::of);

    }

    @Transactional(readOnly = true)
    public Mono<ChallengeResponse> findResponseById(Long id) {
        return this.findById(id)
                .map(ChallengeResponse::of);
    }

    @Transactional(readOnly = true)
    public Flux<ChallengeResponse> findResponseByOwnerId(Long id) {

        return repository.findAllByOwnerIdOrderByStartDateDesc(id)
                .map(ChallengeResponse::of);
    }

    public Mono<ChallengeResponse> changeStatus(Long id, String status) {
        return this.findById(id)
                .map(challenge -> {
                    challenge.changeStatus(ChallengeStatus.valueOf(status));
                    return challenge;
                })
                .flatMap(repository::save)
                .map(ChallengeResponse::of);
    }

    private Mono<Challenge> findById(Long id){
        return repository.findById(id);
    }

    public Flux<ChallengeResponse> findOngoingByOwnerId(Long ownerId) {
        return repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(ownerId, ChallengeStatus.ONGOING)
                .map(ChallengeResponse::of);
    }
}
