package com.boram.look.api.dto;

import com.boram.look.domain.outfit.TemperatureRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class TemperatureRangeDto {

    @Builder
    @Schema(name = "TemperatureRangeDto.Save", description = "삽입시 사용")
    public record Save(
            float min,
            float max
    ) {
        public TemperatureRange toEntity() {
            return TemperatureRange.builder()
                    .min(this.min())
                    .max(this.max())
                    .build();
        }
    }

    @Builder
    @Schema(name = "TemperatureRangeDto.Copy", description = "조회, 수정시 사용")
    public record Copy(
            Integer id,
            float min,
            float max
    ) {
    }
}
