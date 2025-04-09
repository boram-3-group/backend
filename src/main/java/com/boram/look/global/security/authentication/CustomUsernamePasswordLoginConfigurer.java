package com.boram.look.global.security.authentication;

import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.service.auth.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@AllArgsConstructor
@Builder
public class CustomUsernamePasswordLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final String processUrl;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final CustomResponseHandler customResponseHandler;
    private final AuthenticationManager authenticationManager;

    @Override
    public void configure(HttpSecurity http) {
        CustomUsernamePasswordLoginFilter authFilter = CustomUsernamePasswordLoginFilter.builder()
                .objectMapper(this.objectMapper)
                .customResponseHandler(this.customResponseHandler)
                .jwtProvider(this.jwtProvider)
                .authenticationManager(this.authenticationManager)
                .requestMatcher(new AntPathRequestMatcher(processUrl, HttpMethod.POST.name()))
                .build();
        http.addFilterBefore(authFilter, HeaderWriterFilter.class);
    }

}
