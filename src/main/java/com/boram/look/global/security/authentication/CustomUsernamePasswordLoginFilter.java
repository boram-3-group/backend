package com.boram.look.global.security.authentication;

import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.service.auth.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Builder
@AllArgsConstructor
public class CustomUsernamePasswordLoginFilter extends OncePerRequestFilter {

    private CustomResponseHandler customResponseHandler;
    private AuthenticationManager authenticationManager;
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

        LoginRequestDto loginDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication auth = authenticationManager.authenticate(authRequest);

        // 인증 성공 → JWT 생성 & 반환
        String accessToken = jwtProvider.createAccessToken(auth.getName(), auth.getAuthorities());
        String refreshToken = jwtProvider.createRefreshToken(auth.getName());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("access", accessToken)));
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("refresh", refreshToken)));
    }
}
