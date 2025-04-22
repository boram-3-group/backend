package com.boram.look.service.region;

import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

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
}
