package com.boram.look.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public record OAuthJwtDto(String userId, String username, String roleString) {
}
