package com.boram.look.global.security.authentication;

import com.boram.look.global.security.CustomResponseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final CustomResponseHandler responseHandler;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorCode;
        if (exception instanceof BadCredentialsException) {
            errorCode = "AUTH_INVALID_CREDENTIALS";
        } else if (exception instanceof UsernameNotFoundException) {
            errorCode = exception.getMessage();
        } else {
            errorCode = "UNKNOWN";
        }

        responseHandler.onFailure(response, HttpStatus.UNAUTHORIZED, errorCode);
    }
}
