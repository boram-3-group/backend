package com.boram.look.api.controller;

import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AuthController {

    @GetMapping("/oauth/oidc/{registrationId}")
    public String loginPage(@PathVariable String registrationId) {
        StringBuilder builder = new StringBuilder("redirect:");
        OAuth2RegistrationId registration = OAuth2RegistrationId.valueOf(registrationId.toUpperCase());
        switch (registration) {
            case GOOGLE -> builder.append("/oauth2/authorization/google");
            case KAKAO -> builder.append("/oauth2/authorization/kakao");
        }
        return builder.toString();
    }
}
