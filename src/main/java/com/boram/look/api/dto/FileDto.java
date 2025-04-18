package com.boram.look.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FileDto(
        Long fileId,
        String originalFilename,
        String contentType,
        Long size,
        String presignedUrl,
        LocalDateTime uploadedAt
) {
}
