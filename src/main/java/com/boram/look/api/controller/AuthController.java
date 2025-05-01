package com.boram.look.api.controller;

import com.boram.look.api.dto.auth.OAuthJwtDto;
import com.boram.look.api.dto.auth.OIDCTokenResponse;
import com.boram.look.api.dto.user.UserDto;
import com.boram.look.domain.auth.PasswordResetCode;
import com.boram.look.global.util.ResponseUtil;
import com.boram.look.global.ex.NoExistRegistrationException;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import com.boram.look.global.security.oauth.OidcTokenCacheService;
import com.boram.look.service.auth.EmailVerificationService;
import com.boram.look.service.user.UserService;
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

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;
    private final OidcTokenCacheService oidcTokenCacheService;
    private final EmailVerificationService verificationService;
    private final UserService userService;

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
            case NONE -> throw new NoExistRegistrationException();
        }
        return builder.toString();
    }

    @Operation(
            summary = "OIDC 로그인시 access token 토큰 발급",
            description = "callback url에서 보낸 stateId를 이용해 엑세스토큰을 최초에만 발급하는 API"
    )
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 및 리프레시 토큰 발급",
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

    @Deprecated
    @Operation(
            summary = "이메일 인증코드 전송 v1",
            description = "쿼리 스트링 사용으로 인해 v2로 변경 요청드립니다."
    )
    @Hidden
    @PostMapping("/api/v1/auth/send-code")
    public ResponseEntity<?> sendEmailCode_v1(
            @RequestParam String email
    ) {
        verificationService.sendVerificationCode(email);
        return ResponseEntity.ok("인증 코드 전송 완료");
    }

    @Operation(summary = "이메일 인증코드 전송 v2")
    @PostMapping("/api/v2/auth/send-code")
    @Hidden
    public ResponseEntity<?> sendEmailCode_v2(
            @RequestBody String email
    ) {
        verificationService.sendVerificationCode(email);
        return ResponseEntity.ok("인증 코드 전송 완료");
    }

    @Operation(summary = "아이디 찾기 이메일 인증 코드 확인")
    @PostMapping("/api/v1/auth/verify-code")
    @Hidden
    public ResponseEntity<?> verifyEmailCode_v1(
            @RequestParam String email,
            @RequestParam String code
    ) {
        boolean isValid = verificationService.verifyCode(email, code);
        if (isValid) {
            String username = userService.findUsername(email);
            return ResponseEntity.ok(username);
        }
        return ResponseEntity.badRequest().body("인증 실패");
    }

    @Operation(summary = "아이디 찾기 이메일 인증 코드 확인")
    @PostMapping("/api/v2/auth/verify-code")
    @Hidden
    public ResponseEntity<?> verifyEmailCode_v2(
            @RequestBody UserDto.FindUsername dto
    ) {
        boolean isValid = verificationService.verifyCode(dto.email(), dto.code());
        if (isValid) {
            String username = userService.findUsername(dto.email());
            return ResponseEntity.ok(username);
        }
        return ResponseEntity.badRequest().body("인증 실패");
    }

    @Operation(summary = "아이디 찾기 이메일 보내기")
    @PostMapping("/api/v1/auth/username")
    public ResponseEntity<?> findUsername(
            @RequestBody String email
    ) {
        String username = userService.findUsername(email);
        verificationService.sendUsernameEmail(email, username);
        return ResponseEntity.ok("이메일 전송 완료");
    }

    @Operation(summary = "비밀번호 재설정 이메일 보내기")
    @PostMapping("/api/v1/auth/reset-email")
    public ResponseEntity<?> sendResetPasswordEmail(
            @RequestBody UserDto.PasswordResetEmail dto
    ) {
        PasswordResetCode verificationCode = userService.saveVerificationCode(dto);
        verificationService.sendResetPasswordEmail(dto, verificationCode);
        return ResponseEntity.ok("이메일 전송 완료");
    }

    @Operation(summary = "비밀번호 재설정 코드로 비밀번호 변경")
    @PostMapping("/api/v1/auth/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody UserDto.PasswordResetRequest dto
    ) {
        userService.resetUserPassword(dto);
        return ResponseEntity.ok("비밀번호 재설정 완료");
    }

}
