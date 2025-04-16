package com.boram.look.domain.region;

import lombok.Builder;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

@Builder
public record SiGunGuRegion(
        String code,
        String name,
        Coordinate center,
        List<RegionPolygon> polygons,
        GridXY grid
) {
}
