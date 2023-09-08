package com.avatar.challenge.planner.challenge.domain;

import com.avatar.challenge.planner.exception.NotPositiveNumberException;
import com.avatar.challenge.planner.exception.RequiredArgumentException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    @Test
    void create() {
        LocalDate startDate = LocalDate.of(2023, 8, 31);
        Challenge challenge = Challenge.of("pushup 30일 100개 하기", 30, startDate, 1L);

        assertThat(challenge.getEndDate()).isEqualTo(startDate.plusDays(30));
    }

    @Test
    void createFail_null() {
        assertThatThrownBy(() -> Challenge.of(null, 30, LocalDate.of(2023, 8, 31), 1L))
                .isInstanceOf(RequiredArgumentException.class);
        assertThatThrownBy(() -> Challenge.of("pushup 30일 100개 하기", null, LocalDate.of(2023, 8, 31), 1L))
                .isInstanceOf(RequiredArgumentException.class);
        assertThatThrownBy(() -> Challenge.of("pushup 30일 100개 하기", 30, null, 1L))
                .isInstanceOf(RequiredArgumentException.class);
    }

    @Test
    void createFail_periodNotPositive() {
        assertThatThrownBy(() -> Challenge.of("pushup 30일 100개 하기", -1, LocalDate.of(2023, 8, 31), 1L))
                .isInstanceOf(NotPositiveNumberException.class);
        assertThatThrownBy(() -> Challenge.of("pushup 30일 100개 하기", 0, LocalDate.of(2023, 8, 31), 1L))
                .isInstanceOf(NotPositiveNumberException.class);
    }

    @Test
    void changeStatus() {
        Challenge challenge = Challenge.of("pushup 30일 100개 하기", 30, LocalDate.now().plusDays(2), 1L);

        assertThat(challenge.getStatus()).isEqualTo(ChallengeStatus.BEFORE);

        challenge.changeStatus(ChallengeStatus.ONGOING);

        assertThat(challenge.getStatus()).isEqualTo(ChallengeStatus.ONGOING);

        challenge.changeStatus(ChallengeStatus.DROP);

        assertThat(challenge.getStatus()).isEqualTo(ChallengeStatus.DROP);
    }
}