package com.boram.look.global.security.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
@Builder
public class KakaoOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        // Kakao의 토큰 URI
        String tokenUri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        // 요청 구성
        MultiValueMap<String, String> formParameters = this.buildRequestParameter(authorizationGrantRequest);
        HttpHeaders headers = this.buildHeader();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formParameters, headers);

        // 요청 전송 및 응답 수신
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenUri, requestEntity, String.class);
        
        // 응답 파싱 및 반환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        try {
            KakaoTokenResponse kakaoToken = objectMapper.readValue(responseEntity.getBody(), KakaoTokenResponse.class);
            return OAuth2AccessTokenResponse.withToken(kakaoToken.getAccessToken())
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .expiresIn(kakaoToken.getExpiresIn())
                    .refreshToken(kakaoToken.getRefreshToken())
                    .scopes(Set.of(kakaoToken.getScope().split(" ")))
                    .additionalParameters(Map.of("id_token", kakaoToken.getIdToken()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private MultiValueMap<String, String> buildRequestParameter(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
        formParameters.add("grant_type", "authorization_code");
        formParameters.add("client_id", authorizationGrantRequest.getClientRegistration().getClientId());
        formParameters.add("client_secret", authorizationGrantRequest.getClientRegistration().getClientSecret());
        formParameters.add("code", authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
        formParameters.add("redirect_uri", authorizationGrantRequest.getClientRegistration().getRedirectUri());
        return formParameters;
    }

    private HttpHeaders buildHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

}