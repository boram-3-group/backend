package com.boram.look.global.security.oauth;

import com.boram.look.domain.auth.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OidcTokenCacheService {

    private final CacheManager cacheManager;

    public void saveOIDCAccessToken(String stateId, String accessToken) {
        String oidcCacheName = CacheType.OIDC_STATE_ACCESS_TOKEN.getCacheName();
        Cache cache = cacheManager.getCache(oidcCacheName);
        if (cache != null) {
            cache.put(stateId, accessToken);
        }
    }

    public String getOIDCAccessToken(String stateId) {
        String oidcCacheName = CacheType.OIDC_STATE_ACCESS_TOKEN.getCacheName();
        Cache cache = cacheManager.getCache(oidcCacheName);
        if (cache != null) {
            return cache.get(stateId, String.class);
        }
        return null;
    }

    public void evictStateId(String stateId) {
        String oidcCacheName = CacheType.OIDC_STATE_ACCESS_TOKEN.getCacheName();
        Cache cache = cacheManager.getCache(oidcCacheName);
        if (cache != null) {
            cache.evict(stateId);
        }
    }
}
