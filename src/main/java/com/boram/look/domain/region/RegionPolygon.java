package com.boram.look.domain.region;

import lombok.Builder;
import org.locationtech.jts.geom.Geometry;

@Builder
public record RegionPolygon(
        String siGunGuCode,
        String siGunGuNm,
        String admNm,
        String admCd,
        String sidoNm,
        String sidoCode,
        Geometry polygon
) {
}