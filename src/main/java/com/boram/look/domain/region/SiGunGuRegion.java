package com.boram.look.domain.region;

import com.boram.look.api.dto.RegionDto;
import lombok.Builder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

@Builder
public record SiGunGuRegion(
        Long id,
        String sgg,
        String sggnm,
        String sido,
        String sidonm,
        Coordinate center,
        Geometry polygon,
        GridXY grid
) {

    public RegionDto toDto() {
        return RegionDto.builder()
                .id(this.id())
                .sggnm(this.sggnm())
                .sidonm(this.sidonm())
                .build();
    }
}
