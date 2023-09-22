package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.domain.Daily;
import com.avatar.challenge.planner.challenge.domain.DailyList;
import com.avatar.challenge.planner.challenge.domain.DailyRepository;
import com.avatar.challenge.planner.challenge.dto.ChallengeStatusEvent;
import com.avatar.challenge.planner.challenge.dto.DailyRequest;
import com.avatar.challenge.planner.challenge.dto.DailyResponse;
import com.avatar.challenge.planner.exception.BizException;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional
@Service
public class DailyService {

    private final DailyRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public Flux<DailyResponse> createWithChallenge(Long challengeId, Integer period, Long ownerId){
        return repository.findAllByChallengeId(challengeId)
                .count()
                .flatMap(counted -> {
                    if (counted > 0){
                        return Mono.error(new BizException("이미 생성되어 있습니다."));
                    }
                    return Mono.just(challengeId);
                })
                .flatMapMany(id -> {
                    DailyList dailyList = new DailyList();
                    for (int i = 0; i < period; i++) {
                        dailyList.add(Daily.of(id, i+1, ownerId));
                    }
                    return repository.saveAll(dailyList.getDailies());
                })
                .map(DailyResponse::of);
    }

    @Transactional(readOnly = true)
    public Flux<DailyResponse> findAllByChallengeId(Long challengeId, LoginUser loginUser) {
        return repository.findAllByChallengeId(challengeId)
                .filter(daily -> daily.isOwner(loginUser.getId()))
                .switchIfEmpty(Flux.error(new UnauthorizedException()))
                .map(DailyResponse::of);
    }

    public Mono<Void> update(Long id, DailyRequest request, LoginUser loginUser) {
        return findById(id, loginUser)
                .map(daily -> daily.update(request.completed(), request.comment()))
                .flatMap(repository::save)
                .flatMap(daily -> {
                    findIncompleteByChallengeId(daily.getChallengeId(), loginUser)
                            .map(count -> {
                                if (count <= 0){
                                    eventPublisher.publishEvent(new ChallengeStatusEvent(daily.getChallengeId(), ChallengeStatus.SUCCESS.getKey(), loginUser));
                                }
                                return count;
                            }).subscribe();

                    return Mono.just(daily);
                })
                .then();
    }

    @Transactional(readOnly = true)
    public Mono<Long> findIncompleteByChallengeId(Long challengeId, LoginUser loginUser){
        return repository.findAllByChallengeId(challengeId)
                .filter(daily -> daily.isOwner(loginUser.getId()))
                .switchIfEmpty(Flux.error(new UnauthorizedException()))
                .filter(Daily::isIncomplete)
                .count();
    }

    private Mono<Daily> findById(Long id, LoginUser loginUser){
        return repository.findById(id)
                .filter(daily -> daily.isOwner(loginUser.getId()))
                .switchIfEmpty(Mono.error(new UnauthorizedException()));
    }
}
