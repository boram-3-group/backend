package com.boram.look.global.security.reissue;

import com.boram.look.domain.auth.RefreshTokenEntity;
import com.boram.look.domain.auth.repository.RefreshTokenEntityRepository;
import com.boram.look.global.util.ResponseUtil;
import com.boram.look.global.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

//TODO: 코드검토
@Builder
@RequiredArgsConstructor
@Slf4j
public class TokenReissueFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenEntityRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;
    private final RequestMatcher requestMatcher;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = this.getRefreshTokenValue(request);
        if (jwtProvider.isTokenInvalid(refreshToken)) {
            log.info("TokenReissueFilter token is not valid");
            ResponseEntity<?> responseEntity = ResponseUtil.buildUnauthorizedResponseEntity("부적절한 토큰");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(objectMapper.writeValueAsString(responseEntity));
            return;
        }

        String userId = jwtProvider.getUserId(refreshToken);
        String username = jwtProvider.getUsername(refreshToken);

        String deviceId = request.getHeader("X-DEVICE-ID");
        Optional<RefreshTokenEntity> tokenEntityOptional = refreshTokenRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (tokenEntityOptional.isEmpty()) {
            log.info("RefreshTokenEntity token not found");
            ResponseEntity<?> responseEntity = ResponseUtil.buildUnauthorizedResponseEntity("DB에 저장되어 있지 않는 토큰");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(objectMapper.writeValueAsString(responseEntity));
            return;
        }
        RefreshTokenEntity tokenEntity = tokenEntityOptional.get();

        // 새 accessToken + 새 refreshToken 생성
        String newAccessToken = jwtProvider.createAccessToken(username, userId, tokenEntity.getRoleString());
        String newRefreshToken = jwtProvider.createRefreshToken(username, userId, deviceId);

        tokenEntity.update(newRefreshToken);
        refreshTokenRepository.save(tokenEntity);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("access", newAccessToken)));
        jwtProvider.buildRefreshTokenCookie(newRefreshToken, response);
    }


    private String getRefreshTokenValue(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return refreshToken;
    }
}
