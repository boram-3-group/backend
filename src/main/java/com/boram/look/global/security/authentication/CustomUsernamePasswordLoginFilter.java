package com.boram.look.global.security.authentication;

import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.global.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CustomUsernamePasswordLoginFilter extends OncePerRequestFilter {

    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;

    private final AuthenticationManager authenticationManager;
    private final RequestMatcher requestMatcher;
    private final ObjectMapper objectMapper;

    @Builder
    public CustomUsernamePasswordLoginFilter(
            CustomResponseHandler customResponseHandler,
            AuthenticationManager authenticationManager,
            RefreshTokenEntityRepository refreshTokenEntityRepository,
            JwtProvider jwtProvider,
            RequestMatcher requestMatcher,
            ObjectMapper objectMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.requestMatcher = requestMatcher;
        this.objectMapper = objectMapper;
        this.successHandler = new CustomAuthenticationSuccessHandler(jwtProvider, refreshTokenEntityRepository, this.objectMapper);
        this.failureHandler = new CustomAuthenticationFailureHandler(customResponseHandler);
    }

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

        try {
            LoginRequestDto loginDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            Authentication auth = authenticationManager.authenticate(authRequest);
            successHandler.onAuthenticationSuccess(request, response, auth);
        } catch (AuthenticationException ex) {
            failureHandler.onAuthenticationFailure(request, response, ex);
        }

    }

}
