package com.avatar.challenge.planner.challenge.domain;

import com.avatar.challenge.planner.common.BaseEntity;
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
public class Daily extends BaseEntity {

    @Id
    private Long id;

    private Long challengeId;
    private Integer days;
    private Boolean completedAt;
    private String comment;
    private Long ownerId;

    public static Daily of(Long challengeId, Integer day, Long ownerId){
        return new Daily(null, challengeId, day, Boolean.FALSE, null, ownerId);
    }

    public void completion() {
        this.completedAt = Boolean.TRUE;
    }

    public void incomplete(){
        this.completedAt = Boolean.FALSE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Daily daily = (Daily) o;
        return challengeId.equals(daily.challengeId) && days.equals(daily.days) && ownerId.equals(daily.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(challengeId, days, ownerId);
    }
}
