package com.boram.look.global.security.oauth;

import lombok.Builder;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.HashMap;
import java.util.Map;

public class TokenClientRouter implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final Map<String, OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> clients = new HashMap<>();
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> defaultClient;

    @Builder
    public TokenClientRouter(HttpSecurity http) {
        // 기본 OAuth2 client (예: Google, GitHub 등)
        this.defaultClient = new RestClientAuthorizationCodeTokenResponseClient();
        // Kakao 전용 커스터마이징된 토큰 요청 로직
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        this.clientRegistrationRepository = this.getClientRegistrationRepository(context);

        this.clients.put("kakao", KakaoOAuth2AccessTokenResponseClient.builder()
                .clientRegistrationRepository(this.clientRegistrationRepository)
                .build()
        );

    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) throws OAuth2AuthorizationException {
        String registrationId = request.getClientRegistration().getRegistrationId();

        // 등록된 클라이언트가 있으면 사용하고, 없으면 기본 클라이언트 사용
        return clients.getOrDefault(registrationId, defaultClient)
                .getTokenResponse(request);
    }


    private ClientRegistrationRepository getClientRegistrationRepository(ApplicationContext context) {
        return this.clientRegistrationRepository == null ? context.getBean(ClientRegistrationRepository.class) : this.clientRegistrationRepository;
    }

}
