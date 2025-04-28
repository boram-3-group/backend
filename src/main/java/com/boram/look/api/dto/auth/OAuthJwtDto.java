package com.boram.look.api.dto.auth;

import lombok.Builder;

@Builder
public record OAuthJwtDto(String userId, String username, String roleString) {
}
