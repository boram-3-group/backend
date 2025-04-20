package com.boram.look.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "RegionDto", description = "지역구 DTO")
public record RegionDto(
        @Schema(description = "지역구 ID")
        Long id,
        @Schema(description = "시군구 이름 ex) 강남구, 용인시")
        String sggnm,
        @Schema(description = "시도 이름 ex) 서울특별시, 경기도")
        String sidonm
) {
}
