package com.boram.look.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "OPTIONS", "DELETE")
                .allowedOrigins("http://localhost:3000", "https://api.ondolook.click", "http://api.ondolook.click")
                .allowedHeaders("X-DEVICE-ID", "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization")
                .exposedHeaders(String.valueOf(Arrays.asList("Access-Control-Allow-Headers", "Authorization", "x-xsrf-token", "Access-Control-Allow-Headers", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers")))
                .allowCredentials(true);
    }
}
