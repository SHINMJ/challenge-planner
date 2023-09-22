package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.ChallengeRepository;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import com.avatar.challenge.planner.challenge.dto.ChallengeStatusEvent;
import com.avatar.challenge.planner.challenge.dto.DailyResponse;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional
@Service
public class ChallengeService {
    private final ChallengeRepository repository;
    private final DailyService dailyService;

    public Mono<Long> create(ChallengeRequest request, LoginUser loginUser) {
        return repository.save(request.toEntity(loginUser.getId()))
                .map(ChallengeResponse::of)
                .flatMapMany(challengeResponse ->
                    dailyService.createWithChallenge(challengeResponse.getId(), challengeResponse.getPeriod(), challengeResponse.getOwnerId()))
                .last()
                .map(DailyResponse::challengeId);
    }

    @Transactional(readOnly = true)
    public Mono<ChallengeResponse> findResponseById(Long id, LoginUser loginUser) {
        return findByIdWithOwner(id, loginUser)
                .map(ChallengeResponse::of);
    }

    @Transactional(readOnly = true)
    public Flux<ChallengeResponse> findResponseByOwnerId(LoginUser loginUser) {

        return repository.findAllByOwnerIdOrderByStartDateDesc(loginUser.getId())
                .map(ChallengeResponse::of);
    }

    public Mono<ChallengeResponse> changeStatus( Long id, String status, LoginUser loginUser) {
        return findByIdWithOwner(id, loginUser)
                .map(challenge -> {
                    challenge.changeStatus(ChallengeStatus.findByKey(status));
                    return challenge;
                })
                .flatMap(repository::save)
                .map(ChallengeResponse::of);
    }


    @Transactional(readOnly = true)
    public Flux<ChallengeResponse> findOngoingByOwnerId(LoginUser loginUser) {
        return repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(loginUser.getId(), ChallengeStatus.ONGOING)
                .map(ChallengeResponse::of);
    }

    @Transactional(readOnly = true)
    public Flux<ChallengeResponse> findAll(LoginUser loginUser) {
        return repository.findAllByOwnerIdOrderByStartDateDesc(loginUser.getId())
                .map(ChallengeResponse::of);
    }

    @Transactional(readOnly = true)
    public Flux<ChallengeResponse> findResponseByStatus(String status, LoginUser loginUser) {
        return repository.findAllByOwnerIdAndStatusOrderByStartDateDesc(loginUser.getId(), ChallengeStatus.findByKey(status))
                .map(ChallengeResponse::of);
    }

    private Mono<Challenge> findByIdWithOwner(Long id, LoginUser loginUser){
        return repository.findById(id)
                .flatMap(challenge -> {
                    if (!challenge.isOwner(loginUser.getId())){
                        return Mono.error(new UnauthorizedException("권한이 없습니다."));
                    }
                    return Mono.just(challenge);
                });
    }

}
