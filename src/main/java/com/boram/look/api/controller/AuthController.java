package com.boram.look.api.controller;

import com.boram.look.global.security.oauth.OAuth2RegistrationId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class AuthController {

    @GetMapping("/oauth/oidc/{registrationId}")
    public String loginPage(
            @PathVariable String registrationId,
            @RequestParam String callbackUrl
    ) {
        StringBuilder builder = new StringBuilder("redirect:");
        OAuth2RegistrationId registration = OAuth2RegistrationId.valueOf(registrationId.toUpperCase());
        switch (registration) {
            case GOOGLE -> builder.append("/oauth2/authorization/google?state=").append(callbackUrl);
            case KAKAO -> builder.append("/oauth2/authorization/kakao?state=").append(callbackUrl);
        }
        return builder.toString();
    }

    @GetMapping("/test/callback")
    public void callback(
            @RequestParam String userId,
            @RequestBody String accessToken
    ) {
        log.info("callback is called.\nuserId: {}\naccessToken: {}", userId, accessToken);
    }
}
