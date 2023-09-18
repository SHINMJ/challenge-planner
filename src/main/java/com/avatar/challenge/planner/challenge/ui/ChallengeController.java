package com.avatar.challenge.planner.challenge.ui;

import com.avatar.challenge.planner.challenge.application.ChallengeService;
import com.avatar.challenge.planner.challenge.dto.ChallengeRequest;
import com.avatar.challenge.planner.challenge.dto.ChallengeResponse;
import com.avatar.challenge.planner.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/challenges")
public class ChallengeController {
    private final ChallengeService service;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity> create(@RequestBody ChallengeRequest request, @AuthenticationPrincipal LoginUser loginUser){
        return service.create(request, loginUser)
                .map(ChallengeResponse::getId)
                .map(id -> ResponseEntity.created(URI.create("/challenges/"+id)).build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ChallengeResponse> findAll(@AuthenticationPrincipal LoginUser loginUser){
        return service.findAll(loginUser);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ChallengeResponse> findById(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser){
        return service.findResponseById(id, loginUser);
    }

    @PutMapping("/{id}/{status}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changeStatus(@PathVariable Long id, @PathVariable String status, @AuthenticationPrincipal LoginUser loginUser){
        return service.changeStatus(id, status, loginUser).then();
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ChallengeResponse> findByStatus(@PathVariable String status, @AuthenticationPrincipal LoginUser loginUser){
        return service.findResponseByStatus(status, loginUser);
    }
}
