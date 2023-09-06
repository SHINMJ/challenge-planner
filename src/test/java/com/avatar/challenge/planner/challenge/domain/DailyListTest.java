package com.avatar.challenge.planner.challenge.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DailyListTest {

    @Test
    void add5Daily() {
        DailyList list = new DailyList();
        for (int i = 0; i < 5; i++) {
            list.add(Daily.of(1L, i+1, 1L));
        }

        assertThat(list.size()).isEqualTo(5);
    }

    @Test
    void add5SameDaily() {
        DailyList list = new DailyList();
        for (int i = 0; i < 5; i++) {
            list.add(Daily.of(1L, 1, 1L));
        }

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void add5SameDailyButDiffOwner() {
        DailyList list = new DailyList();
        for (int i = 0; i < 5; i++) {
            list.add(Daily.of(1L, 1, Long.valueOf(i)));
        }

        assertThat(list.size()).isEqualTo(5);
    }

    @Test
    void numberOfCompleted() {
        DailyList list = new DailyList();
        for (int i = 0; i < 5; i++) {
            Daily daily = Daily.of(1L, i + 1, 1L);
            if ((i+1) % 2 == 0){
                daily.completion();
            }
            list.add(daily);
        }

        assertThat(list.numberOfCompleted()).isEqualTo(2);
    }
}