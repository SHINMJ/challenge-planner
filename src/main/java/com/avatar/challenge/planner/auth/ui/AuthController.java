package com.avatar.challenge.planner.auth.ui;

import com.avatar.challenge.planner.auth.application.AuthService;
import com.avatar.challenge.planner.auth.dto.JoinRequest;
import com.avatar.challenge.planner.auth.dto.LoginRequest;
import com.avatar.challenge.planner.auth.dto.TokenRequest;
import com.avatar.challenge.planner.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TokenResponse> login(@RequestBody LoginRequest request){
        return service.login(request);
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TokenResponse> reissue(@RequestBody TokenRequest request){
        return service.reissue(request);
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> join(@RequestBody JoinRequest request){
        return service.join(request);
    }
}
