package com.boram.look.global.security.authentication;

import com.boram.look.domain.auth.RefreshTokenEntity;
import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.util.ResponseUtil;
import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.global.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Builder
@AllArgsConstructor
public class CustomUsernamePasswordLoginFilter extends OncePerRequestFilter {

    private CustomResponseHandler customResponseHandler;
    private AuthenticationManager authenticationManager;
    private RefreshTokenEntityRepository refreshTokenEntityRepository;
    private JwtProvider jwtProvider;

    private RequestMatcher requestMatcher;
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        if (!this.requestMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }
        String deviceId = request.getHeader("X-DEVICE-ID");
        LoginRequestDto loginDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication auth = authenticationManager.authenticate(authRequest);
        User loginUser = ((PrincipalDetails) auth.getPrincipal()).getUser();

        // 인증 성공 → JWT 생성 & 반환
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
