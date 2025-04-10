package com.boram.look.global.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secret;
    @Value("${jwt.access_expiration_time}")
    private long accessTokenExpirationTime;
    @Value("${jwt.refresh_expiration_time}")
    private long refreshTokenExpirationTime;
    @Value("${spring.active.profiles")
    private String activeProfiles;

    private final RefreshTokenEntityRepository refreshTokenEntityRepository;


    /**
     * JWT 생성
     */
    public String createAccessToken(
            String username,
            String userId,
            String roleString
    ) {

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + this.accessTokenExpirationTime))
                .withClaim("roles", roleString)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * JWT 생성
     */
    public String createRefreshToken(String username, String userId, String deviceId) {
        String tokenValue = JWT.create()
                .withSubject(username)
                .withClaim("userId", userId)
                .withClaim("deviceId", deviceId)
                .withExpiresAt(new Date(System.currentTimeMillis() + this.refreshTokenExpirationTime))
                .sign(Algorithm.HMAC512(secret));

        return tokenValue;
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
     * JWT에서 사용자 이름 추출
     */
    public String getUserId(String token) {
        return JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(token)
                .getClaim("userId").asString();
    }

    /**
     * JWT 유효성 검증
     */
    public boolean isTokenInvalid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token);
            return false;
        } catch (Exception e) {
            return true;
        }
    }


    public void buildRefreshTokenCookie(String newRefreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }

}
