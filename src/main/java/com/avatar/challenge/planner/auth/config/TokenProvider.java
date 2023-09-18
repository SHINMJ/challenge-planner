package com.avatar.challenge.planner.auth.config;

import com.avatar.challenge.planner.auth.dto.TokenResponse;
import com.avatar.challenge.planner.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth ";
    private static final String DELIMITER = ",";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expired-in}")
    private long expiredIn;
    @Value("${jwt.refresh-expired-in}")
    private long refreshExpiredIn;

    private Key key;
    private JwtParser parser;

    private final ReactiveUserDetailsService reactiveUserDetailsService;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    public TokenResponse generateToken(Authentication authentication){
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);
        return TokenResponse.of(accessToken, refreshToken);
    }

    public Mono<Authentication> getAuthentication(String token){
        Claims claims = parser.parseClaimsJws(token)
                .getBody();
        return reactiveUserDetailsService.findByUsername(claims.getSubject())
                .map(userDetails ->
                        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
    }

    public boolean validate(String token){
        try{
            Claims claims = parser
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 서명입니다.");
            throw new InvalidTokenException("잘못된 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰 입니다.");
            throw new InvalidTokenException("만료된 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰입니다.");
            throw new InvalidTokenException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰이 잘못되었습니다.");
            throw new InvalidTokenException("토큰이 잘못되었습니다.");
        }
    }

    private String generateRefreshToken(Authentication authentication){
        return generate(authentication, refreshExpiredIn);
    }
    private String generateAccessToken(Authentication authentication){
        return generate(authentication, expiredIn);
    }

    private String generate(Authentication authentication, long expired){
        String auth = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(DELIMITER));

        Date now = Date.from(Instant.now());
        Date validity = Date.from(now.toInstant().plusMillis(expired));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, auth)
                .signWith(key)
                .setIssuedAt(now)
                .setExpiration(validity)
                .compact();
    }


}
