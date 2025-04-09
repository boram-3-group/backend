package com.boram.look.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secret;
    @Value("${jwt.access_expiration_time}")
    private long accessTokenExpirationTime;
    @Value("${jwt.refresh_expiration_time}")
    private long refreshTokenExpirationTime;

    /**
     * JWT 생성
     */
    public String createAccessToken(
            String username,
            Collection<? extends GrantedAuthority> authorities
    ) {
        String roleString = "";
        if (authorities != null) {
            roleString = authorities.stream()
                    .filter(Objects::nonNull)
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + this.accessTokenExpirationTime))
                .withClaim("roles", roleString)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * JWT 생성
     */
    public String createRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + this.refreshTokenExpirationTime))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * JWT에서 사용자 이름 추출
     */
    public String getUsername(String token) {
        return JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(token)
                .getSubject();
    }

    /**
     * JWT 유효성 검증
     */
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
