package com.avatar.challenge.planner.challenge.domain;


import com.avatar.challenge.planner.common.BaseEntity;
import com.avatar.challenge.planner.exception.NotPositiveNumberException;
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
public class Challenge extends BaseEntity {
    private static final Integer ZERO = 0;

    @Id
    private Long id;

    private String name;
    private Integer period;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChallengeStatus status;
    private Long ownerId;
    private Boolean sentNotification = Boolean.FALSE;

    public Challenge(String name, Integer period, LocalDate startDate, Long ownerId) {
        validate(name, period, startDate);
        this.name = name;
        this.period = period;
        this.startDate = startDate;
        this.endDate =  startDate.plusDays(period);
        this.ownerId = ownerId;
        setStatus(startDate);
    }

    private void setStatus(LocalDate startDate) {
        if (startDate.isAfter(LocalDate.now())){
            this.status = ChallengeStatus.BEFORE;
            return;
        }
        this.status = ChallengeStatus.ONGOING;
    }

    public static Challenge of(String name, Integer period, LocalDate startDate, Long ownerId){
        return new Challenge(name, period, startDate, ownerId);
    }

    public Challenge changeStatus(ChallengeStatus status){
        this.status = status;
        return this;
    }

    public void updateChallenge(Challenge challenge){
        this.name = challenge.name;
        this.period = challenge.period;
        this.startDate = challenge.startDate;
        this.endDate = challenge.endDate;
        this.status = challenge.status;
    }

    public boolean isOwner(Long ownerId) {
        return this.ownerId.equals(ownerId);
    }

    public Challenge sent(){
        this.sentNotification = Boolean.TRUE;
        return this;
    }

    private void validate(String name, Integer period, LocalDate startDate) {
        if (!StringUtils.hasLength(name)){
            throw new RequiredArgumentException("챌린지 명은 필수입니다.");
        }

        if (period == null){
            throw new RequiredArgumentException("챌린지 기간은 필수입니다.");
        }

        if (period <= ZERO){
            throw new NotPositiveNumberException();
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
