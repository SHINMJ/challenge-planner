package com.avatar.challenge.planner.challenge;

import com.avatar.challenge.planner.AcceptanceTest;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
         * 현재 진행중인 챌린지만 조회
         * 챌린지 상태 변경 (drop -> ongoing)
         * 현재 진행중인 챌린지만 조회
         */
        String challengeName = "푸시업 20일 100개 챌린지";
        webTestClient
                .post()
                .uri(ENDPOINT_PREFIX)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new ChallengeRequest(challengeName, 20, LocalDate.now())), ChallengeRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        List<ChallengeResponse> responses = webTestClient.get()
                .uri(ENDPOINT_PREFIX)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeResponse.class)
                .hasSize(1)
                .consumeWith(result -> {
                    List<ChallengeResponse> responseBody = result.getResponseBody();
                    assertThat(responseBody).allSatisfy(challenge -> {
                        assertThat(challenge.getName()).isEqualTo(challengeName);
                        assertThat(challenge.getStatus()).isEqualTo(ChallengeStatus.ONGOING.getValue());
                    });
                })
                .returnResult()
                .getResponseBody();

        Long challengeId = responses.get(0).getId();

        webTestClient.get()
                .uri(ENDPOINT_PREFIX+"/"+ challengeId)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChallengeResponse.class)
                .consumeWith(result -> {
                    ChallengeResponse responseBody = result.getResponseBody();
                    assertEquals(responseBody.getName(), challengeName);
                    assertEquals(responseBody.getStatus(), ChallengeStatus.ONGOING.getValue());
                });

        webTestClient.put()
                .uri(ENDPOINT_PREFIX+"/"+ challengeId +"/"+ChallengeStatus.DROP.getKey())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody();

        webTestClient.get()
                .uri(ENDPOINT_PREFIX+"/status/ongoing")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeResponse.class)
                .hasSize(0);

        webTestClient.put()
                .uri(ENDPOINT_PREFIX+"/"+ challengeId +"/"+ChallengeStatus.ONGOING.getKey())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody();

        webTestClient.get()
                .uri(ENDPOINT_PREFIX+"/status/ongoing")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeResponse.class)
                .hasSize(1);
    }
}
