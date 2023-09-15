package com.avatar.challenge.planner.user.domain;

import com.avatar.challenge.planner.exception.InvalidArgumentException;
import com.avatar.challenge.planner.exception.RequiredArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void create_fail_required() {
        assertThatThrownBy(() -> User.of(null, "1111", "testUser"))
                .isInstanceOf(RequiredArgumentException.class);
        assertThatThrownBy(() -> User.of("test@email.com", null, "testUser"))
                .isInstanceOf(RequiredArgumentException.class);
    }


    @ParameterizedTest
    @CsvSource(value = {"test", "test@email", ".com"})
    void create_fail_invalidEmail(String email) {
        assertThatThrownBy(() -> User.of(email, "1111", "testUser"))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("이메일 형식이 잘못되었습니다.");
    }

    @Test
    void updatePassword() {
        String password = "1111";
        User testUser = User.of("test@email.com", password, "testUser");
        User updatePassword = testUser.updatePassword("11122");

        assertNotEquals(password, updatePassword.getPassword());
    }

    @Test
    void updateRefreshToken() {
        User testUser = User.of("test@email.com", "1111", "testUser");
        assertNull(testUser.getRefreshToken());

        testUser.updateRefreshToken("refreshToken");
        assertNotNull(testUser.getRefreshToken());

    }
}