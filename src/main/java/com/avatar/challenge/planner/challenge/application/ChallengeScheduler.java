package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.avatar.challenge.planner.challenge.domain.ChallengeRepository;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChallengeScheduler {
    private final ChallengeRepository repository;
    private final DailyService dailyService;


    @Scheduled(cron = "${schedules.cron.notification}")
    public void scheduled(){
        dailyJobSaved().subscribe();
    }

    @Transactional
    public Mono<Long> dailyJobSaved(){
        LocalDate endDate = LocalDate.now().minusDays(1);
        return repository.findAllByEndDate(endDate)
                .flatMap(challenge -> dailyService.findIncompleteByChallengeId(challenge.getId())
                        .count()
                        .flatMap(cnt -> {
                            if (cnt == 0){
                                return Mono.just(challenge.changeStatus(ChallengeStatus.SUCCESS));
                            }
                            return Mono.just(challenge.changeStatus(ChallengeStatus.FAILED));
                        }))
                .flatMap(repository::save)
                .map(this::send)
                .onErrorContinue((throwable, o) -> log.error(throwable.getMessage()))
                .map(Challenge::sent)
                .flatMap(repository::save)
                .filter(challenge -> challenge.getSentNotification().equals(Boolean.TRUE))
                .count();
    }

    // @TODO notification
    Challenge send(Challenge challenge){
        return challenge;
    }
}
