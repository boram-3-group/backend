package com.boram.look.global.security.authentication;

import com.boram.look.domain.auth.RefreshTokenEntity;
import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;
    private final ObjectMapper objectMapper;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        String deviceId = request.getHeader("X-DEVICE-ID");
        User loginUser = ((PrincipalDetails) auth.getPrincipal()).getUser();
        String roleString = ResponseUtil.buildRoleString(auth.getAuthorities());

        String accessToken = jwtProvider.createAccessToken(auth.getName(), loginUser.getId().toString(), roleString);
        String refreshToken = jwtProvider.createRefreshToken(loginUser.getUsername(), loginUser.getId().toString(), deviceId);

        refreshTokenEntityRepository.save(RefreshTokenEntity.builder()
                .userId(loginUser.getId().toString())
                .deviceId(deviceId)
                .refreshTokenValue(refreshToken)
                .roleString(roleString)
                .build());

        ResponseUtil.responseAccessToken(this.objectMapper, response, accessToken);
        ResponseUtil.responseRefreshToken(response, jwtProvider, refreshToken);
    }

}
