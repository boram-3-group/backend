package com.boram.look.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(name = "FileDto", description = "파일 메타데이터 DTO")
public record FileDto(
        @Schema(description = "파일 메타데이터 id")
        Long fileId,
        @Schema(description = "원본파일명")
        String originalFilename,
        @Schema(description = "파일 유형 - image .. etc")
        String contentType,
        @Schema(description = "파일 크기")
        Long size,
        @Schema(description = "파일 임시 조회 url")
        String presignedUrl,
        @Schema(description = "업로드 일시")
        LocalDateTime uploadedAt
) {
}
