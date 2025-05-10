package com.boram.look.api.controller;

import com.boram.look.api.dto.auth.OAuthJwtDto;
import com.boram.look.api.dto.auth.OIDCTokenResponse;
import com.boram.look.api.dto.user.UserDto;
import com.boram.look.domain.auth.PasswordResetCode;
import com.boram.look.domain.auth.RefreshTokenEntity;
import com.boram.look.domain.auth.constants.VerificationConstants;
import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.security.authentication.PrincipalDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;
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
        if (token == null) {
            return ResponseEntity.badRequest().body("state id에 대한 토큰이 null");
        }

        OAuthJwtDto dto = jwtProvider.buildDto(token);
        String issuedToken = jwtProvider.createAccessToken(dto.username(), dto.userId(), dto.roleString());
        String refreshToken = jwtProvider.createRefreshToken(dto.username(), dto.userId(), deviceId);

        refreshTokenEntityRepository.save(RefreshTokenEntity.builder()
                .userId(dto.userId())
                .deviceId(deviceId)
                .refreshTokenValue(refreshToken)
                .roleString(dto.roleString())
                .build());

        ResponseUtil.responseRefreshToken(response, this.jwtProvider, refreshToken);
        UserDto.Profile profile = userService.getUserProfile(dto.userId());
        return ResponseEntity.ok(OIDCTokenResponse.builder()
                .access(issuedToken)
                .profile(profile)
                .build());
    }

    @Operation(summary = "아이디 찾기 이메일 인증코드 전송")
    @PostMapping("/api/v2/auth/send-code")
    public ResponseEntity<?> sendUsernameEmailCode(
            @RequestBody String email
    ) {
        userService.findByEmail(email);
        verificationService.sendVerificationCode(email, email, VerificationConstants.FIND_USERNAME_TYPE_KEY);
        return ResponseEntity.ok("인증 코드 전송 완료");
    }

    @Operation(summary = "아이디 찾기 이메일 인증 코드 확인")
    @PostMapping("/api/v1/auth/verify-code")
    public ResponseEntity<?> verifyEmailCode(
            @RequestBody String code
    ) {
        String email = verificationService.verifyCode(VerificationConstants.FIND_USERNAME_TYPE_KEY, code);
        String username = userService.findUsername(email);
        return ResponseEntity.ok(username);
    }

    @Operation(summary = "비밀번호 재설정 이메일 보내기")
    @PostMapping("/api/v1/auth/reset-email")
    public ResponseEntity<?> sendResetPasswordEmail(
            @RequestBody UserDto.PasswordResetEmail dto
    ) {
        String email = userService.getUserEmail(dto);
        verificationService.sendVerificationCode(email, dto.username(), VerificationConstants.RESET_PASSWORD_TYPE_KEY);
        return ResponseEntity.ok("이메일 전송 완료");
    }

    @Operation(summary = "비밀번호 재설정 이메일 인증 코드 확인")
    @PostMapping("/api/v1/auth/verify-code/password")
    public ResponseEntity<?> verifyEmailCode(
            @RequestBody UserDto.VerifyPasswordEmail dto
    ) {
        String username = verificationService.verifyCode(VerificationConstants.RESET_PASSWORD_TYPE_KEY, dto.code());
        if (username == null) {
            return ResponseEntity.badRequest().body("인증코드와 유저ID가 일치하지 않습니다.");
        }
        return ResponseEntity.ok(username);
    }

    @Operation(summary = "비밀번호 재설정 코드로 비밀번호 변경")
    @PostMapping("/api/v1/auth/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody UserDto.PasswordResetRequest dto
    ) {
        String username = verificationService.verifyCode(VerificationConstants.RESET_PASSWORD_TYPE_KEY, dto.verificationCode());
        userService.resetUserPassword(username, dto.newPassword());
        return ResponseEntity.ok("비밀번호 재설정 완료");
    }

    @Operation(summary = "로그인한 유저의 프로필 조회")
    @GetMapping("/api/v1/auth/profile")
    public ResponseEntity<?> getLoginUserProfile(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserDto.Profile profile = userService.getLoginUserProfile(principalDetails);
        return ResponseEntity.ok(profile);
    }

}
