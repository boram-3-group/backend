package com.boram.look.api.controller;

import com.boram.look.api.dto.OAuthJwtDto;
import com.boram.look.global.ResponseUtil;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    @GetMapping("/oauth/oidc/{registrationId}")
    public String loginPage(
            @PathVariable String registrationId,
            @RequestParam String callbackUrl
    ) {
        StringBuilder builder = new StringBuilder("redirect:");
        OAuth2RegistrationId registration = OAuth2RegistrationId.valueOf(registrationId.toUpperCase());
        String encodedState = Base64.getUrlEncoder().encodeToString(callbackUrl.getBytes(StandardCharsets.UTF_8));
        switch (registration) {
            case GOOGLE -> builder.append("/oauth2/authorization/google?state=").append(encodedState);
            case KAKAO -> builder.append("/oauth2/authorization/kakao?state=").append(encodedState);
        }
        return builder.toString();
    }

    @PostMapping("/oauth/issue/{stateId}")
    public ResponseEntity<?> issueToken(
            HttpServletResponse response,
            @PathVariable String stateId,
            @RequestBody String deviceId
    ) {
        // TODO: 여기서 stateId로 저장된 accestoken 가져오기
        String token = "test";

        OAuthJwtDto dto = jwtProvider.buildDto(token);
        String issuedToken = jwtProvider.createAccessToken(dto.getUsername(), dto.getUserId(), dto.getRoleString());
        String refreshToken = jwtProvider.createRefreshToken(dto.getUsername(), dto.getUserId(), deviceId);

        ResponseUtil.responseRefreshToken(response, this.jwtProvider, refreshToken);
        return ResponseEntity.ok(Map.of("access", issuedToken));
    }

    @GetMapping("/test/callback")
    public void callback(
            @RequestBody String access
    ) {
        log.info("callback is called.\naccessToken: {}", access);
    }
}
