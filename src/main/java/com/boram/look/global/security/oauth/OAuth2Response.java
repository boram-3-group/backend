package com.boram.look.global.security.oauth;

import com.boram.look.domain.user.constants.Gender;

public abstract class OAuth2Response {

    public abstract String id();

    public abstract String nickname();

    public abstract String name();

    public abstract String email();

    public abstract String phoneNumber();

    public abstract String profileImageUrl();

    public abstract Gender gender();

    public abstract OAuth2RegistrationId registrationId();

    protected static String getStringValue(Object value) {
        return value != null ? value.toString() : null;
    }
}
