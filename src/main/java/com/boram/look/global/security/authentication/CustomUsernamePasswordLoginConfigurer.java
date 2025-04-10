package com.boram.look.global.security.authentication;

import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.domain.user.repository.FirebaseTokenRepository;
import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.reissue.TokenReissueFilter;
import com.boram.look.service.user.FirebaseTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@AllArgsConstructor
@Builder
public class CustomUsernamePasswordLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final String processUrl;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final CustomResponseHandler customResponseHandler;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;
    private final FirebaseTokenService firebaseTokenRepository;

    @Override
    public void configure(HttpSecurity http) {
        CustomUsernamePasswordLoginFilter authFilter = CustomUsernamePasswordLoginFilter.builder()
                .objectMapper(this.objectMapper)
                .customResponseHandler(this.customResponseHandler)
                .jwtProvider(this.jwtProvider)
                .authenticationManager(this.authenticationManager)
                .refreshTokenEntityRepository(this.refreshTokenEntityRepository)
                .firebaseTokenService(this.firebaseTokenRepository)
                .requestMatcher(new AntPathRequestMatcher(processUrl, HttpMethod.POST.name()))
                .build();
        http.addFilterAfter(authFilter, TokenReissueFilter.class);
    }

    public CustomUsernamePasswordLoginConfigurer customizer() {
        return this;
    }
}
