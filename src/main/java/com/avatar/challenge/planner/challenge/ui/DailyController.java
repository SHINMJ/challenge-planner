package com.avatar.challenge.planner.challenge.ui;

import com.avatar.challenge.planner.challenge.application.ChallengeService;
import com.avatar.challenge.planner.challenge.application.DailyService;
import com.avatar.challenge.planner.challenge.dto.DailyRequest;
import com.avatar.challenge.planner.challenge.dto.DailyResponse;
import com.avatar.challenge.planner.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dailies")
public class DailyController {

    private final DailyService service;

    @GetMapping("/{challengeId}")
    public Flux<DailyResponse> findAllByChallengeId(@PathVariable Long challengeId, @AuthenticationPrincipal LoginUser loginUser){
        return service.findAllByChallengeId(challengeId, loginUser);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable Long id,
                             @RequestBody DailyRequest request, @AuthenticationPrincipal LoginUser loginUser){
        return service.update(id, request, loginUser);
    }
}
