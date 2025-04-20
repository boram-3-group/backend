package com.boram.look.api.controller;

import com.boram.look.api.dto.OutfitDto;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.outfit.OutfitService;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.WeatherCacheService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outfit")
@RequiredArgsConstructor
@Slf4j
public class OutfitController {
    private final OutfitService outfitService;
    private final RegionCacheService regionCacheService;
    private final WeatherCacheService weatherCacheService;

    @Operation(description = "파라메터 값에 해당하는 코디들을 출력")
    @GetMapping
    public ResponseEntity<?> getOutfitByPosition(
            @Parameter(description = "경도 (Longitude)") @RequestParam(name = "longitude") float longitude,
            @Parameter(description = "위도 (Latitude)") @RequestParam(name = "latitude") float latitude,
            @Parameter(description = "이벤트 유형 ID") @RequestParam(name = "event-type-id") Integer eventTypeId,
            @Parameter(description = "성별 (MALE / FEMALE / NONE)") @RequestParam(name = "gender") Gender gender
    ) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(latitude, longitude).orElseThrow(ResourceNotFoundException::new);
        List<Forecast> forecasts = weatherCacheService.getForecast(region.id());
        OutfitDto.Transfer dto = outfitService.matchOutfit(eventTypeId, forecasts, gender);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Hidden
    public ResponseEntity<?> insertOutfit(@RequestBody OutfitDto.Insert dto) {
        outfitService.insertOutfit(dto);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PutMapping("/{outfitId}")
    @Hidden
    public ResponseEntity<?> updateOutfit(
            @RequestBody OutfitDto.Insert dto,
            @PathVariable Long outfitId
    ) {
        outfitService.updateOutfit(dto, outfitId);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PostMapping(value = "/{outfitId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Hidden
    public ResponseEntity<?> insertOutfitImages(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "dtos") List<OutfitDto.Image> dtos,
            @PathVariable Long outfitId
    ) {
        outfitService.insertOutfitImages(principalDetails, images, dtos, outfitId);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @DeleteMapping("/{outfitId}")
    @Hidden
    public ResponseEntity<?> updateOutfit(
            @PathVariable Long outfitId
    ) {
        outfitService.deleteOutfit(outfitId);
        return ResponseEntity.noContent().build();
    }

}
