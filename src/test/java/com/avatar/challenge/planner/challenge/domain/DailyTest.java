package com.avatar.challenge.planner.challenge.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DailyTest {

    @Test
    void create() {
        Daily day_1 =  Daily.of(1L, 1, 1L);

        assertThat(day_1.getCompleteAt()).isFalse();
    }

    @Test
    void toggle() {
        Daily day_1 =  Daily.of(1L, 1, 1L);

        assertThat(day_1.getCompleteAt()).isFalse();

        day_1.completion();

        assertThat(day_1.getCompleteAt()).isTrue();

        day_1.incomplete();

        assertThat(day_1.getCompleteAt()).isFalse();
    }
}
