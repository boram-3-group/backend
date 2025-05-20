package com.boram.look.controller;

import com.boram.look.service.auth.EmailVerificationService;
import com.boram.look.service.user.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class UserControllerBeanConfig {
    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public EmailVerificationService emailVerificationService() {
        return mock(EmailVerificationService.class);
    }

}
