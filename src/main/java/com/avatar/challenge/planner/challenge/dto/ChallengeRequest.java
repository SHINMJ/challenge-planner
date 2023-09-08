package com.avatar.challenge.planner.challenge.dto;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ChallengeRequest {
    private final String name;
    private final Integer period;
    private final LocalDate startDate;

    public Challenge toEntity(Long ownerId){
        return Challenge.of(name, period, startDate, ownerId);
    }
}
