package com.avatar.challenge.planner.challenge.dto;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import lombok.*;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeResponse {
    private Long id;
    private String name;
    private Integer period;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long ownerId;
    private Boolean sent;

    public static ChallengeResponse of(Challenge challenge){
        return new ChallengeResponse(challenge.getId(), challenge.getName(), challenge.getPeriod(), challenge.getStartDate(), challenge.getEndDate(), challenge.getStatus().toString(), challenge.getOwnerId(), challenge.getSentNotification());
    }
}
