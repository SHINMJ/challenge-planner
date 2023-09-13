package com.avatar.challenge.planner.auth.application;

import com.avatar.challenge.planner.auth.config.TokenProvider;
import com.avatar.challenge.planner.auth.dto.LoginRequest;
import com.avatar.challenge.planner.auth.dto.TokenResponse;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final TokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;

    public Mono<TokenResponse> login(LoginRequest request) {
        if (request.isEmpty()){
            return Mono.error(new UnauthorizedException("로그인 정보를 입력하세요."));
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        return authenticationManager.authenticate(authenticationToken)
                .onErrorMap(error -> new UnauthorizedException(error.getMessage()))
                .map(tokenProvider::generateToken);
    }
}
