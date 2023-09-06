package com.avatar.challenge.planner.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("daily")
public class Daily {

    @Id
    private Long id;

    private Long challengeId;
    private Integer day;
    private Boolean isComplete;
    private String memo;
    private Long ownerId;

    public static Daily of(Long challengeId, Integer day, Long ownerId){
        return new Daily(null, challengeId, day, Boolean.FALSE, null, ownerId);
    }

    public void completion() {
        this.isComplete = Boolean.TRUE;
    }

    public void incomplete(){
        this.isComplete = Boolean.FALSE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Daily daily = (Daily) o;
        return challengeId.equals(daily.challengeId) && day.equals(daily.day) && ownerId.equals(daily.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(challengeId, day, ownerId);
    }
}
