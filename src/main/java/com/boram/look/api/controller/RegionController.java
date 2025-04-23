package com.boram.look.api.controller;

import com.boram.look.api.dto.RegionDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.service.region.GeoJsonRegionMapper;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.region.RegionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @Hidden
    @PostMapping("/upload")
    public ResponseEntity<?> uploadGeoJsonToDb(@RequestPart MultipartFile file) throws Exception {
        GeoJsonRegionMapper geoJsonRegionMapper = new GeoJsonRegionMapper();
        File tempFile = File.createTempFile("upload", ".geojson");
        file.transferTo(tempFile);
        List<SiGunGuRegion> regions = geoJsonRegionMapper.buildRegionGeoJson(tempFile);
        tempFile.delete();
        Long firstId = regionService.saveBulkEntities(regions);
        regionCacheService.loadRegionMap();
        return ResponseEntity.created(URI.create("/api/v1/region/" + firstId)).body("행정 구역 정보 업로드 완료");
    }

    @PostMapping("/merge-sido")
    public ResponseEntity<?> mergeSidoRegions() {
        regionService.mergeSidoRegions();
        return ResponseEntity.created(URI.create("/api/v1/region/")).body("시도 지역 병합 완료");
    }

    @Operation(
            summary = "지역 조회",
            description = "위도, 경도를 입력하고 그 위,경도에 위치한 지역이 어디인지 출력"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 지역 정보를 반환함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegionDto.class)
            )
    )
    @GetMapping
    public ResponseEntity<?> getRegionsFromPoint(
            @Parameter(description = "위도 (Latitude)") @RequestParam double lat,
            @Parameter(description = "경도 (Longitude)") @RequestParam double lon
    ) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(region.toDto());
    }
}
