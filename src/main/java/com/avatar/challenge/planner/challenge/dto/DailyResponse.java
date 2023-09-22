package com.avatar.challenge.planner.challenge.dto;

import com.avatar.challenge.planner.challenge.domain.Daily;

public record DailyResponse(Long id, Long challengeId, Integer days, Boolean completedAt, String comment, Long ownerId) {

    public static DailyResponse of(Daily daily){
        return new DailyResponse(daily.getId(), daily.getChallengeId(), daily.getDays(), daily.getCompletedAt(), daily.getComment(), daily.getOwnerId());
    }
}
