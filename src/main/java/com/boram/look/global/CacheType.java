package com.boram.look.global;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum CacheType {

    OIDC_STATE_ACCESS_TOKEN("oidcStateAccessToken", Duration.ofMinutes(5)),
    REFRESH_TOKEN("refreshTokenCache", Duration.ofDays(14));

    private final String cacheName;
    private final Duration ttl;

    CacheType(String cacheName, Duration ttl) {
        this.cacheName = cacheName;
        this.ttl = ttl;
    }

}
