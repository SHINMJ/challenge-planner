package com.avatar.challenge.planner.user.infra;

import com.avatar.challenge.planner.exception.UserNotFoundException;
import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.domain.UserRepository;
import com.avatar.challenge.planner.user.dto.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<User> userMono = userRepository.findByEmail(username)
                .log("findbyusername === ")
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
        return userMono
                .map(LoginUser::new);
    }
}
