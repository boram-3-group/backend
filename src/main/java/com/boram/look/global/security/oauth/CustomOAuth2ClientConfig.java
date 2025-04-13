package com.boram.look.global.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
@RequiredArgsConstructor
public class CustomOAuth2ClientConfig {

    private final OAuth2ClientProperties oAuth2ClientProperties;

    @Bean
    public ClientRegistrationRepository customClientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                googleClientRegistration(),
                kakaoClientRegistration()
        );
    }

    private ClientRegistration googleClientRegistration() {
        OAuth2ClientProperties.Google reg = oAuth2ClientProperties.getRegistration().getGoogle();
        OAuth2ClientProperties.Google prov = oAuth2ClientProperties.getProvider().getGoogle();

        return ClientRegistration.withRegistrationId(OAuth2RegistrationId.GOOGLE.getRegistrationId())
                .clientId(reg.getClientId())
                .clientSecret(reg.getClientSecret())
                .redirectUri(reg.getRedirectUri())
                .authorizationUri(prov.getAuthorizationUri())
                .tokenUri(prov.getTokenUri())
                .userInfoUri(prov.getUserInfoUri())
                .userNameAttributeName("sub")
                .clientName("Google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope(reg.getScope())
                .jwkSetUri(prov.getJwkSetUri())
                .build();
    }

    private ClientRegistration kakaoClientRegistration() {
        OAuth2ClientProperties.Kakao reg = oAuth2ClientProperties.getRegistration().getKakao();
        OAuth2ClientProperties.Kakao prov = oAuth2ClientProperties.getProvider().getKakao();

        return ClientRegistration.withRegistrationId(OAuth2RegistrationId.KAKAO.getRegistrationId())
                .clientId(reg.getClientId())
                .clientSecret(reg.getClientSecret())
                .redirectUri(reg.getRedirectUri())
                .authorizationUri(prov.getAuthorizationUri())
                .tokenUri(prov.getTokenUri())
                .userInfoUri(prov.getUserInfoUri())
                .userNameAttributeName("sub")
                .clientName("Kakao")
                .jwkSetUri(prov.getJwkSetUri())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope(reg.getScope())
                .build();
    }
}
