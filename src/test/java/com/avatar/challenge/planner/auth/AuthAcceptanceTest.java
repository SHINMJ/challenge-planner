package com.avatar.challenge.planner.auth;

import com.avatar.challenge.planner.AcceptanceTest;
import com.avatar.challenge.planner.auth.config.TokenProvider;
import com.avatar.challenge.planner.auth.dto.JoinRequest;
import com.avatar.challenge.planner.auth.dto.LoginRequest;
import com.avatar.challenge.planner.auth.dto.TokenRequest;
import com.avatar.challenge.planner.auth.dto.TokenResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.avatar.challenge.planner.utils.DatabaseInitialize.TEST_PASSWORD;
import static com.avatar.challenge.planner.utils.DatabaseInitialize.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;

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
                .expectBody()
                .jsonPath("accessToken", String.class).isNotEmpty();
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

    @Test
    void refresh_success() {

        webTestClient
                .post()
                .uri(ENDPOINT_PREFIX+"/reissue")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new TokenRequest(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken())), TokenRequest.class)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(TokenResponse.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .consumeNextWith(token -> {
                    assertAll(
                            () -> assertNotNull(tokenResponse.getAccessToken(), token.getAccessToken()),
                            () -> assertNotNull(tokenResponse.getRefreshToken(), token.getRefreshToken())
                    );
                })
                .verifyComplete();
    }

    @Test
    void refresh_fail_invalidrefreshtoken() {
        webTestClient
                .post()
                .uri(ENDPOINT_PREFIX+"/reissue")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new TokenRequest(tokenResponse.getAccessToken(), "refreshtoken")), TokenRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void join_fail_existsEmail() {
        webTestClient
                .post()
                .uri(ENDPOINT_PREFIX+"/join")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new JoinRequest(TEST_USER, TEST_PASSWORD, "testUser")), JoinRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

    }

    @Test
    void join_success() {
        webTestClient
                .post()
                .uri(ENDPOINT_PREFIX+"/join")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new JoinRequest("test1@email.com", TEST_PASSWORD, "testUser1")), JoinRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
    }
}
