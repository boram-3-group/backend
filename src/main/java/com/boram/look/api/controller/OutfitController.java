package com.boram.look.api.controller;

import com.boram.look.api.dto.outfit.OutfitDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.user.constants.Gender;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.outfit.OutfitFacade;
import com.boram.look.service.outfit.OutfitService;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/outfit")
@RequiredArgsConstructor
@Slf4j
public class OutfitController {
    private final OutfitService outfitService;
    private final OutfitFacade outfitFacade;

    @Operation(
            summary = "코디 조회",
            description = "파라메터 값에 해당하는 코디들을 출력"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 코디 정보를 반환함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OutfitDto.Transfer.class)
            )
    )
    @GetMapping
    public ResponseEntity<?> getOutfitByPosition(
            @Parameter(description = "경도 (Longitude)") @RequestParam(name = "longitude") float longitude,
            @Parameter(description = "위도 (Latitude)") @RequestParam(name = "latitude") float latitude,
            @Parameter(description = "이벤트 유형 ID") @RequestParam(name = "event-type-id") Integer eventTypeId,
            @Parameter(description = "성별 (MALE / FEMALE / NONE)") @RequestParam(name = "gender") Gender gender,
            @AuthenticationPrincipal PrincipalDetails principal // null 가능
    ) {
        OutfitDto.Transfer dto = outfitFacade.getOutfitByPosition(longitude, latitude, eventTypeId, gender, principal);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Hidden
    public ResponseEntity<?> insertOutfit(@RequestBody OutfitDto.Insert dto) {
        outfitService.insertOutfit(dto);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{outfitId}")
    @Hidden
    public ResponseEntity<?> updateOutfit(
            @RequestBody OutfitDto.Insert dto,
            @PathVariable Long outfitId
    ) {
        outfitService.updateOutfit(dto, outfitId);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{outfitId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertOutfitImages(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(name = "images", value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "dtos") List<OutfitDto.Image> dtos,
            @PathVariable Long outfitId
    ) {
        outfitService.insertOutfitImages(principalDetails, images, dtos, outfitId);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{outfitId}")
    @Hidden()
    public ResponseEntity<?> updateOutfit(
            @PathVariable Long outfitId
    ) {
        outfitService.deleteOutfit(outfitId);
        return ResponseEntity.noContent().build();
    }

}
