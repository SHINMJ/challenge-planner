package com.avatar.challenge.planner.config;

import com.avatar.challenge.planner.auth.config.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {
    private static final String PERMIT_PATTERN = "/auth/**";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Bean
    @DependsOn({"methodSecurityExpressionHandler"})
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager reactiveAuthenticationManager,
                                                         AuthenticationWebFilter authenticationWebFilter
    ){
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec
                                .authenticationEntryPoint(((exchange, ex) -> Mono.fromRunnable(() -> {
                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                })))
                                .accessDeniedHandler(((exchange, denied) -> Mono.fromRunnable(() -> {
                                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                })))
                )
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(PERMIT_PATTERN).permitAll()
                        .anyExchange().authenticated()
                )
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterBefore(authenticationWebFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(){
        ReactiveAuthenticationManager authenticationManager = Mono::just;

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return authenticationWebFilter;
    }

    private ServerAuthenticationConverter serverAuthenticationConverter(){
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String token = resolveToken(request);
            if (request.getPath().toString().startsWith(PERMIT_PATTERN)){
                return Mono.empty();
            }

            if (StringUtils.hasText(token) && tokenProvider.validate(token)){
                return tokenProvider.getAuthentication(token);
            }

            return Mono.empty();
        };

    }

    private String resolveToken(ServerHttpRequest request){
        String bearerToken = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

}
