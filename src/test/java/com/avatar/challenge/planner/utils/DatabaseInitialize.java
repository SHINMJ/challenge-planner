package com.avatar.challenge.planner.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@ActiveProfiles("test")
public class DatabaseInitialize {
    public static final String TEST_USER = "test@email.com";
    public static final String TEST_PASSWORD = "11111";

    @Value("classpath:init.sql")
    private Resource initSql;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<Void> execute() {
        String query = null;
        try {
            query = StreamUtils.copyToString(initSql.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return template.
                getDatabaseClient()
                .sql(query)
                .then();
    }

    @Transactional
    public Mono<Void> saveInitData(){
        String encodePassword = passwordEncoder.encode(TEST_PASSWORD);
        return template.getDatabaseClient()
                .sql("insert into users (email, password, nickname) values ('"+TEST_USER+"', '"+encodePassword+"', 'testUser');")
                .then();

    }
}
