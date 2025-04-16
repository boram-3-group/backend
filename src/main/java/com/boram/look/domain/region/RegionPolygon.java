package com.boram.look.domain.region;

import lombok.Builder;
import org.locationtech.jts.geom.Geometry;

@Builder
public record RegionPolygon(
        String siGunGuCode,
        String admNm,
        Geometry polygon
) {
}