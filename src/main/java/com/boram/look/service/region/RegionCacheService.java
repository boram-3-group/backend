package com.boram.look.service.region;


import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.repository.RegionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RegionCacheService {

    private final RegionRepository regionRepository;
    private final Map<Long, SiGunGuRegion> cache = new ConcurrentHashMap<>();

    public Map<Long, SiGunGuRegion> cache() {
        return cache;
    }

    @PostConstruct
    public void loadRegionMap() {
        WKTReader reader = new WKTReader();
        for (Region e : regionRepository.findAll()) {
            try {
                Geometry geom = reader.read(e.getPolygonText());
                GridXY grid = new GridXY(e.getNx(), e.getNy());
                Coordinate center = new Coordinate(e.getLon(), e.getLat());

                SiGunGuRegion region = SiGunGuRegion.builder()
                        .id(e.getId())
                        .sgg(e.getSgg())
                        .sggnm(e.getSggnm())
                        .sido(e.getSido())
                        .sidonm(e.getSidonm())
                        .center(center)
                        .polygon(geom)
                        .grid(grid)
                        .build();
                cache.put(e.getId(), region);
            } catch (Exception ex) {
                throw new RuntimeException("Polygon 파싱 실패: " + e.getSgg() + " " + e.getSido(), ex);
            }
        }
    }

    public Optional<SiGunGuRegion> findByCode(Long code) {
        return Optional.ofNullable(cache.get(code));
    }

    /**
     * Point-in-Polygon
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 해당 위 경도가 속한 시군구 지역 데이터
     */
    public Optional<SiGunGuRegion> findRegionByLocation(double latitude, double longitude) {
        GeometryFactory factory = new GeometryFactory();
        Point userPoint = factory.createPoint(new Coordinate(longitude, latitude));

        // 캐시에서 전체 시군구 꺼내서 검사
        Collection<SiGunGuRegion> allRegions = this.cache.values();

        for (SiGunGuRegion region : allRegions) {
            if (region.polygon().covers(userPoint)) {
                return Optional.of(region);
            }
        }

        // 어디에도 포함되지 않는 경우
        return Optional.empty();
    }

}
