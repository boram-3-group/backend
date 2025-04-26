package com.boram.look.api.dto;

import com.boram.look.domain.condition.TemperatureRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

public class TemperatureRangeDto {

    @Builder
    @Schema(name = "TemperatureRangeDto.Save", description = "삽입시 사용")
    public record Save(
            BigDecimal min,
            BigDecimal max
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
            BigDecimal min,
            BigDecimal max
    ) {
    }
}
