package com.avatar.challenge.planner.challenge.dto;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@RequiredArgsConstructor
public class ChallengeResponse {
    private final Long id;
    private final String name;
    private final Integer period;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;
    private final Long ownerId;

    public static ChallengeResponse of(Challenge challenge){
        return new ChallengeResponse(challenge.getId(), challenge.getName(), challenge.getPeriod(), challenge.getStartDate(), challenge.getEndDate(), challenge.getStatus().toString(), challenge.getOwnerId());
    }
}
