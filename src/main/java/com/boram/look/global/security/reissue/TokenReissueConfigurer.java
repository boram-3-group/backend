package com.boram.look.global.security.reissue;

import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Builder
@AllArgsConstructor
public class TokenReissueConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtProvider jwtProvider;
    private final RefreshTokenEntityRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;
    private final String processUrl;

    @Override
    public void configure(HttpSecurity builder) {
        TokenReissueFilter tokenReissueFilter = TokenReissueFilter.builder()
                .jwtProvider(this.jwtProvider)
                .refreshTokenRepository(this.refreshTokenRepository)
                .objectMapper(this.objectMapper)
                .requestMatcher(new AntPathRequestMatcher(this.processUrl, HttpMethod.POST.name()))
                .build();
        builder.addFilterBefore(tokenReissueFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public TokenReissueConfigurer customizer() {
        return this;
    }
}
