package com.boram.look.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class OIDCTokenResponse {
    @Schema(description = "발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String access;

}
