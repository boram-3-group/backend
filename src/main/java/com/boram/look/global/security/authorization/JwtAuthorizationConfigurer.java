package com.boram.look.global.security.authorization;

import com.boram.look.global.security.authentication.CustomUsernamePasswordLoginFilter;
import com.boram.look.global.security.authentication.PrincipalDetailsService;
import com.boram.look.service.auth.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@AllArgsConstructor
@Builder
public class JwtAuthorizationConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtProvider jwtProvider;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    public void configure(HttpSecurity http) {
        JwtAuthorizationFilter authorizationFilter = JwtAuthorizationFilter.builder()
                .jwtProvider(this.jwtProvider)
                .principalDetailsService(this.principalDetailsService)
                .build();
        http.addFilterBefore(authorizationFilter, CustomUsernamePasswordLoginFilter.class);
    }

}
