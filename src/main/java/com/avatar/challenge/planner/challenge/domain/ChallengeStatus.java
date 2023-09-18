package com.avatar.challenge.planner.challenge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ChallengeStatus {
    BEFORE("before", "BEFORE"),
    ONGOING("ongoing", "ONGOING"),
    SUCCESS("success", "SUCCESS"),
    DROP("drop", "DROP"),
    FAILED("failed", "FAILED");

    private final String key;
    private final String value;

    public static ChallengeStatus findByKey(String key){
        return Arrays.stream(ChallengeStatus.values())
                .filter(status -> status.key.equals(key))
                .findAny().orElse(null);
    }
}
