package com.avatar.challenge.planner;

import com.avatar.challenge.planner.utils.DatabaseInitialize;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseInitialize databaseInitialize;

    @Autowired
    private ApplicationContext context;

    protected WebTestClient webTestClient;

    @PostConstruct
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(context)
                .configureClient()
                .entityExchangeResultConsumer(System.out::println)
                .build();
    }

    @BeforeEach
    void setUp() {
        databaseInitialize.execute().subscribe();
        databaseInitialize.saveInitData().subscribe();
    }
}
