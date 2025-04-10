package com.boram.look.global.config;


import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.security.CustomAccessDeniedHandler;
import com.boram.look.global.security.CustomAuthenticationEntryPoint;
import com.boram.look.global.security.CustomResponseHandler;
import com.boram.look.global.security.authentication.CustomUsernamePasswordLoginConfigurer;
import com.boram.look.global.security.authentication.PrincipalDetailsService;
import com.boram.look.global.security.authorization.JwtAuthorizationConfigurer;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.reissue.TokenReissueConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Spring Security 설정을 정의하는 클래스입니다.
 * <p>
 * 이 클래스는 OAuth2 인증과 기본적인 보안 필터 체인을 구성합니다. 주요 보안 설정으로는
 * JWT 기반 인증, OAuth2 로그인, 예외 처리 핸들링 등이 포함됩니다.
 * </p>
 * <p>
 * 또한, 특정 URL에 대한 권한 설정과 로그인 성공 후 처리를 위한 핸들러가 정의됩니다.
 * </p>
 */
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final PrincipalDetailsService principalDetailsService;
    private final CustomResponseHandler customResponseHandler;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;


    /**
     * Spring Security의 기본 보안 필터 체인을 구성합니다.
     * <p>
     * 이 메서드는 애플리케이션의 보안 설정을 정의하며, OAuth2 로그인 및 JWT 기반 인증을 처리합니다.
     * 요청 경로에 대한 인증 요구 사항을 설정하고, CSRF 보호, 로그인 폼, 기본 인증 등을 비활성화합니다.
     * </p>
     *
     * @param http HttpSecurity를 통해 보안 설정을 정의합니다.
     * @return 구성된 SecurityFilterChain 객체
     * @throws Exception 보안 설정 중 오류가 발생할 경우 발생합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(getAuthenticationNotRequiredUrl()).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(customAuthenticationEntryPoint())
                                .accessDeniedHandler(customAccessDeniedHandler())
                )
                .with(
                        tokenReissueConfigurer(),
                        TokenReissueConfigurer::customizer
                )
                .with(
                        customUsernamePasswordLoginConfigurer(),
                        CustomUsernamePasswordLoginConfigurer::customizer
                )
                .with(
                        jwtAuthorizationConfigurer(),
                        JwtAuthorizationConfigurer::customizer
                )
                .build();
    }

    /**
     * 인증 없이 접근할 수 있는 URL 경로를 정의합니다.
     * <p>
     * 특정 API 및 Swagger 관련 URL 등에 대해서는 인증 없이 접근을 허용하도록 설정합니다.
     * </p>
     *
     * @return 인증이 필요하지 않은 URL 경로들을 포함한 RequestMatcher 객체
     */
    private RequestMatcher getAuthenticationNotRequiredUrl() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/h2-console/**"),
                new AntPathRequestMatcher("/healthy"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/swagger/**"),
                new AntPathRequestMatcher("/api/v1/user", HttpMethod.POST.name())
        );
    }

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    private CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }

    private CustomUsernamePasswordLoginConfigurer customUsernamePasswordLoginConfigurer() {
        return CustomUsernamePasswordLoginConfigurer.builder()
                .customResponseHandler(this.customResponseHandler)
                .jwtProvider(this.jwtProvider)
                .objectMapper(this.objectMapper)
                .authenticationManager(this.authenticationManager)
                .refreshTokenEntityRepository(this.refreshTokenEntityRepository)
                .processUrl("/api/v1/auth/login")
                .build();
    }

    private JwtAuthorizationConfigurer jwtAuthorizationConfigurer() {
        return JwtAuthorizationConfigurer.builder()
                .jwtProvider(this.jwtProvider)
                .principalDetailsService(this.principalDetailsService)
                .authenticationManager(this.authenticationManager)
                .build();
    }

    private TokenReissueConfigurer tokenReissueConfigurer() {
        return TokenReissueConfigurer.builder()
                .jwtProvider(this.jwtProvider)
                .objectMapper(this.objectMapper)
                .refreshTokenRepository(this.refreshTokenEntityRepository)
                .processUrl("/api/v1/auth/reissue")
                .build();
    }


}