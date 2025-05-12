package com.boram.look.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateProvider {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);    // 연결 시도 제한 시간 (5초)
        factory.setReadTimeout(25000);      // 응답 대기 시간 (25초)
        return new RestTemplate(factory);
    }
}
