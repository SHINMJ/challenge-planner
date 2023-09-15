package com.avatar.challenge.planner.user.application;

import com.avatar.challenge.planner.exception.BizException;
import com.avatar.challenge.planner.user.domain.User;
import com.avatar.challenge.planner.user.domain.UserRepository;
import com.avatar.challenge.planner.user.dto.UserRequest;
import com.avatar.challenge.planner.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Void> updateRefreshToken(String email, String refreshToken){
        return repository.findByEmail(email)
                .map(user -> user.updateRefreshToken(refreshToken))
                .flatMap(repository::save)
                .then();
    }

    public Mono<UserResponse> create(UserRequest request) {
        return repository.findByEmail(request.getEmail())
                .flatMap(user -> {
                    if (user != null) {
                        return Mono.error(new BizException("이미 사용중인 이메일입니다."));
                    }
                    return request.toEntity();
                })
                .switchIfEmpty(request.toEntity())
                .map(user -> user.updatePassword(passwordEncoder.encode(request.getPassword())))
                .flatMap(repository::save)
                .map(UserResponse::of);

    }

    @Transactional(readOnly = true)
    public Mono<String> findTokenByRefreshToken(String refreshToken) {
        return repository.findByRefreshToken(refreshToken)
                .map(User::getRefreshToken);
    }
}
