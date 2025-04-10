package com.boram.look.global.security.authorization;

import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.authentication.CustomUsernamePasswordLoginFilter;
import com.boram.look.global.security.authentication.PrincipalDetailsService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.web.bind.annotation.ControllerAdvice;

@RequiredArgsConstructor
@Builder
public class JwtAuthorizationConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtProvider jwtProvider;
    private final PrincipalDetailsService principalDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void configure(HttpSecurity http) {
        JwtAuthorizationFilter authorizationFilter = JwtAuthorizationFilter.builder()
                .jwtProvider(this.jwtProvider)
                .principalDetailsService(this.principalDetailsService)
                .authenticationManager(this.authenticationManager)
                .build();
        http.addFilterAfter(authorizationFilter, CustomUsernamePasswordLoginFilter.class);
    }

    public JwtAuthorizationConfigurer customizer() {
        return this;
    }
}
