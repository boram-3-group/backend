package com.boram.look.global.security.reissue;

import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.authorization.JwtAuthorizationConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

@Builder
@AllArgsConstructor
public class TokenReissueConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtProvider jwtProvider;
    private final RefreshTokenEntityRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void configure(HttpSecurity builder) {
        TokenReissueFilter tokenReissueFilter = TokenReissueFilter.builder()
                .jwtProvider(this.jwtProvider)
                .refreshTokenRepository(this.refreshTokenRepository)
                .objectMapper(this.objectMapper)
                .build();
        builder.addFilterBefore(tokenReissueFilter, HeaderWriterFilter.class);
    }

    public TokenReissueConfigurer customizer() {
        return this;
    }
}
