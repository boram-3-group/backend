package com.boram.look.global.security.password;

import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.service.auth.AuthService;
import com.boram.look.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class CustomUsernamePasswordLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private String processUrl;
    private ObjectMapper objectMapper;
    private AuthenticationManager authenticationManager;

    private AuthService authService;
    private UserService userService;
    private CustomResponseHandler customResponseHandler;

    public CustomUsernamePasswordLoginConfigurer processUrl(String processUrl) {
        this.processUrl = processUrl;
        return this;
    }

    public void objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(HttpSecurity http) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        CustomUsernamePasswordLoginFilter authFilter = CustomUsernamePasswordLoginFilter.builder()
                .authService(getAuthService(context))
                .userService(getUserService(context))
                .objectMapper(getObjectMapper())
                .authenticationManager(getAuthManager(context))
                .customResponseHandler(getCustomResponseHandler(context))
                .requestMatcher(new AntPathRequestMatcher(processUrl, HttpMethod.POST.name()))
                .build();
        http.addFilterBefore(authFilter, HeaderWriterFilter.class);
    }

    private AuthService getAuthService(ApplicationContext context) {
        return this.authService == null ? context.getBean(AuthService.class) : this.authService;
    }

    private UserService getUserService(ApplicationContext context) {
        return this.userService == null ? context.getBean(UserService.class) : this.userService;
    }

    private CustomResponseHandler getCustomResponseHandler(ApplicationContext context) {
        return this.customResponseHandler == null ? context.getBean(CustomResponseHandler.class) : this.customResponseHandler;
    }

    private ObjectMapper getObjectMapper() {
        return this.objectMapper == null ? new ObjectMapper() : this.objectMapper;
    }

    private AuthenticationManager getAuthManager(ApplicationContext context) {
        return this.authenticationManager == null ? context.getBean(AuthenticationManager.class) : this.authenticationManager;
    }
}
