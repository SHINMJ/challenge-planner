package com.avatar.challenge.planner.challenge;

import com.avatar.challenge.planner.AcceptanceTest;
import com.avatar.challenge.planner.challenge.domain.ChallengeStatus;
import com.avatar.challenge.planner.challenge.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChallengeAcceptanceTest extends AcceptanceTest {
    private static final String ENDPOINT_PREFIX = "/challenges";
    private static final String ENDPOINT_DAILY_PREFIX = "/dailies";

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
        HttpHeaders responseHeaders = webTestClient
                .post()
                .uri(ENDPOINT_PREFIX)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new ChallengeRequest(challengeName, 2, LocalDate.now())), ChallengeRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseEntity.class)
                .returnResult()
                .getResponseHeaders();

        String id = responseHeaders.getFirst("Location").substring(ENDPOINT_PREFIX.length() + 1);
        Long challengeId = Long.parseLong(id);

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
                .expectBody().isEmpty();

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
                .expectBody().isEmpty();

        webTestClient.get()
                .uri(ENDPOINT_PREFIX+"/status/ongoing")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeResponse.class)
                .hasSize(1);
    }

    @Test
    void dailyManage() {
        /**
         * 로그인되어있다.
         * 2일 챌린지가 등록되어 있다.
         * 1일차 완료
         * 2일차 완료
         * 챌린지 정보 조회 -> 챌린지 상태 완료
         */
        String challengeName = "푸시업 2일 100개 챌린지";

        HttpHeaders responseHeaders = webTestClient
                .post()
                .uri(ENDPOINT_PREFIX)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new ChallengeRequest(challengeName, 2, LocalDate.now())), ChallengeRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseEntity.class)
                .returnResult()
                .getResponseHeaders();

        String challengeId = responseHeaders.getFirst("Location").substring(ENDPOINT_PREFIX.length() + 1);

        List<DailyResponse> dailyResponses = webTestClient.get()
                .uri(ENDPOINT_DAILY_PREFIX + "/" + challengeId)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DailyResponse.class)
                .hasSize(2)
                .returnResult()
                .getResponseBody();


        assertNotNull(dailyResponses);

        for (DailyResponse dailyResponse : dailyResponses) {
            Long dailyId = dailyResponse.id();

            webTestClient.put()
                    .uri(ENDPOINT_DAILY_PREFIX + "/" + dailyId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(new DailyRequest(Boolean.TRUE, "comment : " + dailyId)), DailyRequest.class)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody();
        }

        webTestClient.get()
                .uri(ENDPOINT_PREFIX+"/"+ challengeId)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenResponse.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChallengeResponse.class)
                .consumeWith(result -> {
                    ChallengeResponse responseBody = result.getResponseBody();
                    assertEquals(responseBody.getName(), challengeName);
                    assertEquals(responseBody.getStatus(), ChallengeStatus.SUCCESS.getValue());
                });
    }
}
