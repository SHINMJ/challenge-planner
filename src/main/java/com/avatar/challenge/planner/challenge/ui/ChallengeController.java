package com.avatar.challenge.planner.challenge.ui;

import com.avatar.challenge.planner.challenge.application.ChallengeService;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/challenges")
public class ChallengeController {
    private final ChallengeService service;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> create(@RequestBody ChallengeRequest request, @AuthenticationPrincipal LoginUser loginUser){
        return service.create(request, loginUser).then();
    }
}
