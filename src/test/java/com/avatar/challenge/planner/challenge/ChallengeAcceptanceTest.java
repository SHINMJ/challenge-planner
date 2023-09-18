package com.avatar.challenge.planner.challenge;

import com.avatar.challenge.planner.AcceptanceTest;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public class ChallengeAcceptanceTest extends AcceptanceTest {
    private static final String ENDPOINT_PREFIX = "/challenges";

    @Test
    void soloChallengeManage() {
        /**
         * 로그인 되어 있음.
         * 챌린지 등록
         * 챌린지 목록 조회
         * 챌린지 하나 조회
         * 챌린지 상태 변경 (ongoing -> drop)
         * 챌린지 상태 변경 (drop -> ongoing)
         */
        webTestClient
                .post()
                .uri(ENDPOINT_PREFIX)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new ChallengeRequest("푸시업 20일 100개 챌린지", 20, LocalDate.now())), ChallengeRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
    }
}
