package com.boram.look;

import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.authentication.PrincipalDetailsService;
import com.boram.look.global.security.oauth.OidcTokenCacheService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class SecurityConfigTestBean {
    @Bean
    public JwtProvider jwtProvider() {
        return mock(JwtProvider.class);
    }

    @Bean
    public PrincipalDetailsService principalDetailsService() {
        return mock(PrincipalDetailsService.class);
    }

    @Bean
    public CustomResponseHandler customResponseHandler() {
        return mock(CustomResponseHandler.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return mock(AuthenticationManager.class);
    }

    @Bean
    public RefreshTokenEntityRepository refreshTokenEntityRepository() {
        return mock(RefreshTokenEntityRepository.class);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock(ClientRegistrationRepository.class);
    }

    @Bean
    public OidcTokenCacheService oidcTokenCacheService() {
        return mock(OidcTokenCacheService.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return mock(CorsConfigurationSource.class);
    }

}
