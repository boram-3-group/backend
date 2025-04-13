package com.boram.look.global.security.oauth;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2RegistrationId {
    KAKAO("kakao"),
    GOOGLE("google");

    private final String registrationId;

    public String getRegistrationId() {
        return registrationId;
    }
}
