package com.boram.look.domain.region.cache;

import lombok.Builder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

@Builder
public record SidoRegionCache(
        Long id,
        String sido,
        String sidonm,
        //TODO: api 의 시도 이름 키
        String apiKey,
        Coordinate center,
        Geometry polygon
) {
}
