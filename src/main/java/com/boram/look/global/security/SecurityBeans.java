package com.boram.look.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityBeans {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://api.ondolook.click",
                "http://api.ondolook.click"
        ));
        config.setAllowedMethods(List.of("HEAD", "GET", "PUT", "POST", "OPTIONS", "DELETE"));
        config.setAllowedHeaders(List.of(
                "X-DEVICE-ID", "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"
        ));
        config.setExposedHeaders(List.of(
                "Access-Control-Allow-Headers", "Authorization", "x-xsrf-token", "Origin",
                "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
