package com.avatar.challenge.planner.challenge.dto;

import com.avatar.challenge.planner.user.dto.LoginUser;

public record ChallengeStatusEvent(Long challengeId, String status, LoginUser loginUser) {
}
