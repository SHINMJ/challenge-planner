package com.avatar.challenge.planner.challenge.application;

import com.avatar.challenge.planner.challenge.dto.ChallengeStatusEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public record ChallengeEventHandler(
        ChallengeService challengeService) {

    @EventListener
    public void updateStatus(ChallengeStatusEvent event) {
        challengeService.changeStatus(event.challengeId(), event.status(), event.loginUser())
                .subscribe();
    }
}
