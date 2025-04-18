package com.boram.look.api.controller;

import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.boram.look.service.region.GeoJsonRegionMapper;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.region.RegionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
@RequiredArgsConstructor
@Slf4j
public class RegionController {

    private final RegionService regionService;
    private final RegionCacheService regionCacheService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadGeoJsonToDb(@RequestPart MultipartFile file) throws Exception {
        GeoJsonRegionMapper geoJsonRegionMapper = new GeoJsonRegionMapper();
        File tempFile = File.createTempFile("upload", ".geojson");
        file.transferTo(tempFile);
        List<SiGunGuRegion> regions = geoJsonRegionMapper.buildRegionGeoJson(tempFile);
        tempFile.delete();
        List<Region> entities = geoJsonRegionMapper.toRegionEntities(regions);
        regionService.saveBulkEntities(entities);
        regionCacheService.loadRegionMap();
        //TODO: URI 집어넣기
        return ResponseEntity.created(URI.create("hasdhasd")).body("행정구역 정보 업로드 완료 (" + entities.size() + "건)");
    }

    @GetMapping
    public ResponseEntity<?> getRegionsFromPoint(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(region.toString());
    }
}
