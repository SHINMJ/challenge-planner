package com.avatar.challenge.planner.challenge.domain;

import com.avatar.challenge.planner.exception.NotPositiveNumberException;
import com.avatar.challenge.planner.exception.RequiredArgumentException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Period {
    private static final Integer ZERO = 0;

    private final Integer period;

    protected Period(Integer period) {
        validate(period);
        this.period = period;
    }

    public static Period valueOf(Integer period){
        return new Period(period);
    }

    private void validate(Integer period) {
        if (period == null){
            throw new RequiredArgumentException("챌린지 기간은 필수입니다.");
        }

        if (period <= ZERO){
            throw new NotPositiveNumberException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period1 = (Period) o;
        return period.equals(period1.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period);
    }
}
