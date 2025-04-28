package com.boram.look.api.dto.outfit;

import com.boram.look.domain.user.constants.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class OutfitDto {

    @Builder
    public record Insert(
            Integer eventTypeId,
            Integer temperatureRangeId,
            Gender gender
    ) {
    }

    public record Image(
            String title,
            String description
    ) {
    }

    @Builder
    @Schema(name = "OutfitDto.Transfer", description = "코디정보")
    public record Transfer(
            @Schema(description = "코디 id")
            Long id,
            @Schema(description = "행사 타입 이름")
            String eventType,
            @Schema(description = "온도 범위 문자열")
            String temperatureRange,
            @Schema(description = "코디 이미지 정보")
            List<OutfitImageDto> fileMetadata
    ) {
    }

}
