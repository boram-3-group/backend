package com.boram.look.global.security.oauth;


import com.boram.look.domain.user.entity.User;
import com.boram.look.global.ResponseUtil;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Builder
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final OidcTokenCacheService oidcTokenCacheService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.debug("CustomOAuth2AuthenticationSuccessHandler#onAuthenticationSuccess: {}", oAuth2User);

        String registrationIdString = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        OAuth2RegistrationId registrationId = OAuth2RegistrationId.valueOf(registrationIdString.toUpperCase());

        OAuth2Response oAuth2Response = switch (registrationId) {
            case GOOGLE -> GoogleOAuth2Response.from(oAuth2User.getAttributes());
            case KAKAO -> KakaoOAuth2Response.from(oAuth2User.getAttributes());
        };
        log.debug("CustomOAuth2AuthenticationSuccessHandler#onAuthenticationSuccess: {}", oAuth2Response);
        User loginUser = userService.findOrCreateUser(oAuth2Response);

        // JWT 같은 토큰 생성 (JwtProvider는 커스텀 JWT 생성 클래스)
        // 인증 성공 → JWT 생성 & 반환
        String roleString = ResponseUtil.buildRoleString(loginUser.getRoles());

        String stateEncoded = request.getParameter("state");
        String urlDecoded = URLDecoder.decode(stateEncoded, StandardCharsets.UTF_8);

        String accessToken = jwtProvider.createAccessToken(loginUser.getUsername(), loginUser.getId().toString(), roleString);
        String stateId = ResponseUtil.getStateIdFromCallbackUrl(urlDecoded);

        oidcTokenCacheService.saveOIDCAccessToken(stateId, accessToken);
        String redirectUri = new String(Base64.getUrlDecoder().decode(urlDecoded), StandardCharsets.UTF_8);
        response.sendRedirect(redirectUri);
    }

}