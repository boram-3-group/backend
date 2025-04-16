package com.boram.look.service.region;


import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.RegionPolygon;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.repository.RegionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionCacheService {

    private final RegionRepository regionRepository;
//    private final GeometryFactory geometryFactory = new GeometryFactory();

    private final Map<String, SiGunGuRegion> cache = new HashMap<>();

    @PostConstruct
    public void loadRegionMap() {
        WKTReader reader = new WKTReader();
        for (Region e : regionRepository.findAll()) {
            try {
                Geometry polygon = reader.read(e.getPolygonText());
                RegionPolygon rp = new RegionPolygon(e.getCode(), e.getName(), polygon);
                GridXY grid = new GridXY(e.getNx(), e.getNy());
                Coordinate center = new Coordinate(e.getLon(), e.getLat());

                SiGunGuRegion region = SiGunGuRegion.builder()
                        .code(e.getCode())
                        .name(e.getName())
                        .center(center)
                        .polygons(List.of(rp))
                        .grid(grid)
                        .build();
                cache.put(e.getCode(), region);
            } catch (Exception ex) {
                throw new RuntimeException("Polygon 파싱 실패: " + e.getCode(), ex);
            }
        }
    }

    public Optional<SiGunGuRegion> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

}
