package com.boram.look.api.dto;

import lombok.Builder;

@Builder
public record PresignedUrlDto(
        Long fileId,
        String presignedUrl
) {
}
