package com.boram.look.api.controller;

import com.boram.look.api.dto.OAuthJwtDto;
import com.boram.look.api.dto.OIDCTokenResponse;
import com.boram.look.global.ResponseUtil;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import com.boram.look.global.security.oauth.OidcTokenCacheService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;
    private final OidcTokenCacheService oidcTokenCacheService;

    @Operation(
            summary = "oauth 로그인 호출",
            description = "OAuth 시작 RestAPI"
    )
    @GetMapping("/oauth/oidc/{registrationId}")
    public String loginPage(
            @Parameter(description = "제공자 ID - kakao, google") @PathVariable String registrationId,
            @Parameter(description = "콜백url - 로그인 완료시 리다이렉션시킬 프론트엔드 url") @RequestParam String callbackUrl
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

    @Operation(
            summary = "OIDC 로그인시 access token 토큰 발급",
            description = "callback url에서 보낸 stateId를 이용해 엑세스토큰을 최초에만 발급하는 API"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 온도 범위 데이터 조회함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OIDCTokenResponse.class)
            )
    )
    @PostMapping("/oauth/issue/{stateId}")
    public ResponseEntity<?> issueToken(
            HttpServletResponse response,
            @Parameter(description = "사용자 식별 ID - 프론트에서 발급") @PathVariable String stateId,
            @Parameter(description = "제공자 ID - kakao, google") @RequestHeader("X-DEVICE-ID") String deviceId
    ) {
        String token = oidcTokenCacheService.getOIDCAccessToken(stateId);
        OAuthJwtDto dto = jwtProvider.buildDto(token);
        String issuedToken = jwtProvider.createAccessToken(dto.username(), dto.userId(), dto.roleString());
        String refreshToken = jwtProvider.createRefreshToken(dto.username(), dto.userId(), deviceId);
        ResponseUtil.responseRefreshToken(response, this.jwtProvider, refreshToken);
        return ResponseEntity.ok(OIDCTokenResponse.builder().access(issuedToken).build());
    }

    @Hidden
    @GetMapping("/test/callback")
    public ResponseEntity<?> callback(
            @RequestParam String stateId
    ) {
        log.info("callback is called.\nstateId: {}", stateId);
        return ResponseEntity.ok(Map.of("state", stateId));
    }
}
