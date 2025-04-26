package com.boram.look.service.region;


import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.entity.SidoRegion;
import com.boram.look.domain.region.repository.RegionRepository;
import com.boram.look.domain.region.repository.SidoRegionRepository;
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
    private final SidoRegionRepository sidoRegionRepository;
    private final Map<Long, SiGunGuRegion> regionCache = new ConcurrentHashMap<>();
    private final Map<Long, SidoRegionCache> sidoCache = new ConcurrentHashMap<>();

    public Map<Long, SiGunGuRegion> regionCache() {
        return regionCache;
    }

    public Map<Long, SidoRegionCache> sidoCache() {
        return sidoCache;
    }

    public void loadRegionMap() {
        this.regionCache.clear();
        this.sidoCache.clear();
        this.initSiGunGuCache();
        this.initSidoCache();
    }

    public void initSiGunGuCache() {
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
                regionCache.put(e.getId(), region);
            } catch (Exception ex) {
                throw new RuntimeException("Polygon 파싱 실패: " + e.getSgg() + " " + e.getSido(), ex);
            }
        }
    }

    public void initSidoCache() {
        WKTReader reader = new WKTReader();
        for (SidoRegion e : sidoRegionRepository.findAll()) {
            try {
                Geometry geom = reader.read(e.getPolygonText());
                SidoRegionCache sidoRegion = SidoRegionCache.builder()
                        .id(e.getId())
                        .sido(e.getSido())
                        .center(new Coordinate(e.getLon(), e.getLat()))
                        .apiKey(e.getApiKey())
                        .polygon(geom)
                        .build();
                sidoCache.put(e.getId(), sidoRegion);
            } catch (Exception ex) {
                throw new RuntimeException("Polygon 파싱 실패: " + e.getSido(), ex);
            }
        }
    }


    public Optional<SiGunGuRegion> findByCode(Long code) {
        return Optional.ofNullable(regionCache.get(code));
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
        Collection<SiGunGuRegion> allRegions = this.regionCache.values();

        for (SiGunGuRegion region : allRegions) {
            if (region.polygon().covers(userPoint)) {
                return Optional.of(region);
            }
        }

        // 어디에도 포함되지 않는 경우
        return Optional.empty();
    }

    public Optional<SidoRegionCache> findSidoRegionByLocation(double latitude, double longitude) {
        GeometryFactory factory = new GeometryFactory();
        Point userPoint = factory.createPoint(new Coordinate(longitude, latitude));

        // 캐시에서 전체 시군구 꺼내서 검사
        Collection<SidoRegionCache> allRegions = this.sidoCache().values();

        for (SidoRegionCache region : allRegions) {
            if (region.polygon().covers(userPoint)) {
                return Optional.of(region);
            }
        }

        // 어디에도 포함되지 않는 경우
        return Optional.empty();
    }

}
