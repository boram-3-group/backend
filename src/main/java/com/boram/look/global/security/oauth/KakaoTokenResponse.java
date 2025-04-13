package com.boram.look.global.security.oauth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoTokenResponse {
    private String idToken;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String scope;
    private Long refreshTokenExpiresIn;
}
