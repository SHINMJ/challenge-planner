package com.avatar.challenge.planner.challenge.dto;

import com.avatar.challenge.planner.challenge.domain.Challenge;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeRequest {
    private String name;
    private Integer period;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    public Challenge toEntity(Long ownerId){
        return Challenge.of(name, period, startDate, ownerId);
    }
}
