package com.boram.look.api.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
public record OAuthJwtDto(String userId, String username, String roleString) {
}
