package com.boram.look.global.security.authorization;

import com.boram.look.global.security.JwtProvider;
import com.boram.look.global.security.authentication.PrincipalDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtProvider jwtProvider;
    private final PrincipalDetailsService principalDetailsService;

    @Builder
    public JwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider,
            PrincipalDetailsService principalDetailsService
    ) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.principalDetailsService = principalDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
//            log.info("header is not a valid authorization header\nrequest url: {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (jwtProvider.isTokenInvalid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            log.info("token is not valid");
            return;
        }

        String username = jwtProvider.getUsername(token);
        UserDetails userDetails = principalDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}

