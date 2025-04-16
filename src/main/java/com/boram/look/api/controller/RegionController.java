package com.boram.look.api.controller;

import com.boram.look.domain.region.entity.Region;
import com.boram.look.service.region.GeoJsonRegionMapper;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.region.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
@RequiredArgsConstructor
@Slf4j
public class RegionController {

    private final RegionService regionService;
    private final GeoJsonRegionMapper geoJsonRegionMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadGeoJsonToDb(MultipartFile file) {
        try {
            File geoJson = new File("./HangJeongDong_ver20250401.geojson");
            geoJsonRegionMapper.buildRegionGeoJson(geoJson);
            List<Region> entities = geoJsonRegionMapper.toRegionEntities();
            regionService.saveBulkEntities(entities);
            //TODO: URI 집어넣기
            return ResponseEntity.created(URI.create("hasdhasd")).body("행정구역 정보 업로드 완료 (" + entities.size() + "건)");
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }
}
