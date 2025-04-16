package com.boram.look.service.region;

import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public void saveBulkEntities(List<Region> entities) {
        regionRepository.saveAll(entities);
    }
}
