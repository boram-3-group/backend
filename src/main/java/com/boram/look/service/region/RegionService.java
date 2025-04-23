package com.boram.look.service.region;

import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.entity.SidoRegion;
import com.boram.look.domain.region.repository.RegionRepository;
import com.boram.look.domain.region.repository.SidoRegionRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;
    private final SidoRegionRepository sidoRegionRepository;

    public Long saveBulkEntities(List<SiGunGuRegion> regions) {
        List<Region> buildEntities = this.toRegionEntities(regions);
        List<Region> entities = regionRepository.saveAll(buildEntities);
        return entities.getFirst().getId();
    }

    public List<Region> toRegionEntities(List<SiGunGuRegion> regions) {
        return regions.stream()
                .map(siGunGu -> {
                    String polygonWkt = siGunGu.polygon().toText(); // 첫 폴리곤만 사용
                    return Region.builder()
                            .sgg(siGunGu.sgg())
                            .sggnm(siGunGu.sggnm())
                            .sido(siGunGu.sido())
                            .sidonm(siGunGu.sidonm())
                            .lat(siGunGu.center().y)
                            .lon(siGunGu.center().x)
                            .polygonText(polygonWkt)
                            .nx(siGunGu.grid().nx())
                            .ny(siGunGu.grid().ny())
                            .build();
                }).collect(Collectors.toList());
    }

    public void mergeSidoRegions() {
        List<Region> regions = regionRepository.findAll();
        GeoJsonRegionMapper regionMapper = new GeoJsonRegionMapper();
        Map<String, Geometry> sidoMap = regionMapper.mergeSidoPolygons(regions);

        List<SidoRegion> toSave = new ArrayList<>();
        for (Map.Entry<String, Geometry> entry : sidoMap.entrySet()) {
            SidoRegion region = this.convertToSidoRegion(entry.getKey(), entry.getValue());
            toSave.add(region);
        }

        sidoRegionRepository.saveAll(toSave);
    }

    public SidoRegion convertToSidoRegion(String sido, Geometry geometry) {
        Point centroid = geometry.getCentroid();
        Coordinate center = centroid.getCoordinate();

        return SidoRegion.builder()
                .sido(sido)
                .polygonText(geometry.toText())
                .lat(center.y)
                .lon(center.x)
                .build();
    }

}
