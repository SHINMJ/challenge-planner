package com.avatar.challenge.planner.auth.application;

import com.avatar.challenge.planner.auth.config.TokenProvider;
import com.avatar.challenge.planner.auth.dto.JoinRequest;
import com.avatar.challenge.planner.auth.dto.LoginRequest;
import com.avatar.challenge.planner.auth.dto.TokenRequest;
import com.avatar.challenge.planner.auth.dto.TokenResponse;
import com.avatar.challenge.planner.exception.InvalidTokenException;
import com.avatar.challenge.planner.exception.UnauthorizedException;
import com.avatar.challenge.planner.user.application.UserService;
import com.avatar.challenge.planner.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Transactional
@RequiredArgsConstructor
@Service
public class AuthService {

    private final TokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final UserService userService;

    public Mono<TokenResponse> login(LoginRequest request) {
        if (request.isEmpty()){
            return Mono.error(new UnauthorizedException("로그인 정보를 입력하세요."));
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        return authenticationManager.authenticate(authenticationToken)
                .onErrorMap(error -> new UnauthorizedException(error.getMessage()))
                .log("authentication token")
                .map(tokenProvider::generateToken)
                .log("generate token")
                .flatMap(tokenResponse -> updateRefreshToken(request.getEmail(), tokenResponse));
    }

    public Mono<TokenResponse> reissue(TokenRequest request) {
        try {
            tokenProvider.validate(request.getRefreshToken());
        }catch (Exception e) {
            return Mono.error(new InvalidTokenException(e.getMessage()));
        }

        return userService.findTokenByRefreshToken(request.getRefreshToken())
                .flatMap(refreshToken ->
                    tokenProvider.getAuthentication(refreshToken)
                            .zipWhen(authentication -> Mono.just(tokenProvider.generateToken(authentication)))
                )
                .onErrorMap(error -> new UnauthorizedException(error.getMessage()))
                .flatMap(tuple -> updateRefreshToken(tuple.getT1().getName(), tuple.getT2()));
    }

    public Mono<Void> join(JoinRequest request) {
        return userService.create(new UserRequest(request.getEmail(), request.getPassword(), request.getNickname()))
                .then();
    }

    private Mono<TokenResponse> updateRefreshToken(String email, TokenResponse tokenResponse){
        return userService.updateRefreshToken(email, tokenResponse.getRefreshToken())
                .thenReturn(tokenResponse);
    }

}
