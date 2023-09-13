package com.avatar.challenge.planner.auth;

import com.avatar.challenge.planner.AcceptanceTest;
import com.avatar.challenge.planner.auth.dto.LoginRequest;
import com.avatar.challenge.planner.auth.dto.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static com.avatar.challenge.planner.utils.DatabaseInitialize.TEST_PASSWORD;
import static com.avatar.challenge.planner.utils.DatabaseInitialize.TEST_USER;

public class AuthAcceptanceTest extends AcceptanceTest {
    private static final String ENDPOINT_PREFIX = "/auth";

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest(TEST_USER, TEST_PASSWORD);
        this.webTestClient.post()
                .uri(ENDPOINT_PREFIX+"/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(TokenResponse.class);
    }

    @Test
    void login_fail_invalidPassword() {
        LoginRequest request = new LoginRequest(TEST_USER, "111");
        this.webTestClient.post()
                .uri(ENDPOINT_PREFIX+"/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody();
    }

    @Test
    void login_fail_noArgs() {
        LoginRequest request = new LoginRequest(null, "111");
        this.webTestClient.post()
                .uri(ENDPOINT_PREFIX+"/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("message").isEqualTo("로그인 정보를 입력하세요.");
    }
}
