package com.boram.look.domain.auth.constants;

import java.time.Duration;

public class VerificationConstants {
    public static final Duration AUTH_VERIFICATION_EXPIRE_TIME = Duration.ofMinutes(4);
    public static final Duration JOIN_EMAIL_VERIFICATION_EXPIRE_TIME = Duration.ofMinutes(10);

    public static final String AUTH_VERIFICATION_KEY_PREFIX = "verify:";
    public static final String FIND_USERNAME_TYPE_KEY = "username:";
    public static final String RESET_PASSWORD_TYPE_KEY = "password:";
    public static final String JOIN_TYPE_KEY = "join:";

    public static final String EMAIL_HISTORY_KEY = "email-history:";
}
