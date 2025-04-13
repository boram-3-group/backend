package com.boram.look.global.security.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
@Getter
@Setter
public class OAuth2ClientProperties {
    private Registration registration;
    private Provider provider;

    @Getter
    @Setter
    public static class Registration {
        private Google google;
        private Kakao kakao;
    }

    @Getter
    @Setter
    public static class Provider {
        private Google google;
        private Kakao kakao;
    }

    @Getter
    @Setter
    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private List<String> scope;

        // Provider 쪽
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String jwkSetUri;
    }

    @Getter
    @Setter
    public static class Kakao {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private List<String> scope;

        // Provider 쪽
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String jwkSetUri;
    }
    
}
