package com.avatar.challenge.planner;

import com.avatar.challenge.planner.auth.config.TokenProvider;
import com.avatar.challenge.planner.auth.dto.TokenResponse;
import com.avatar.challenge.planner.utils.DatabaseInitialize;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.avatar.challenge.planner.utils.DatabaseInitialize.TEST_PASSWORD;
import static com.avatar.challenge.planner.utils.DatabaseInitialize.TEST_USER;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseInitialize databaseInitialize;

    @Autowired
    private ApplicationContext context;

    @Autowired
    TokenProvider tokenProvider;

    protected WebTestClient webTestClient;
    protected TokenResponse tokenResponse;

    @PostConstruct
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(context)
                .configureClient()
                .entityExchangeResultConsumer(System.out::println)
                .build();
    }

    @BeforeEach
    void setUp() {
        tokenResponse = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(TEST_USER, TEST_PASSWORD));
        databaseInitialize.execute().subscribe();
        databaseInitialize.saveInitData(tokenResponse.getRefreshToken()).subscribe();
    }
}
