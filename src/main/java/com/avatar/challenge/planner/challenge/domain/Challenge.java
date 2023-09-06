package com.avatar.challenge.planner.challenge.domain;


import com.avatar.challenge.planner.common.BaseDomain;
import com.avatar.challenge.planner.exception.RequiredArgumentException;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("challenge")
public class Challenge extends BaseDomain {
    @Id
    private Long id;

    private String name;
    private Period period;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChallengeStatus status;

    public Challenge(String name, Period period, LocalDate startDate) {
        validate(name, startDate);
        this.name = name;
        this.period = period;
        this.startDate = startDate;
        this.endDate =  startDate.plusDays(period.getPeriod());
        this.status = ChallengeStatus.BEFORE;
    }

    public static Challenge of(String name, Integer period, LocalDate startDate){
        return new Challenge(name, Period.valueOf(period), startDate);
    }

    public void changeStatus(ChallengeStatus status){
        this.status = status;
    }

    private void validate(String name, LocalDate startDate) {
        if (!StringUtils.hasLength(name)){
            throw new RequiredArgumentException("챌린지 명은 필수입니다.");
        }

        if(startDate == null){
            throw new RequiredArgumentException("챌린지 시작일은 필수입니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return id.equals(challenge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
