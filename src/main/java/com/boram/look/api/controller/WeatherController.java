package com.boram.look.api.controller;

import com.boram.look.api.dto.AirQualityDto;
import com.boram.look.api.dto.WeatherDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.global.util.TimeUtil;
import com.boram.look.service.air.AirQualityService;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.WeatherCacheService;
import com.boram.look.service.weather.WeatherService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController {
    private final WeatherService weatherService;
    private final RegionCacheService regionCacheService;
    private final WeatherCacheService weatherCacheService;
    private final AirQualityService airQualityService;


    @GetMapping("/air")
    public ResponseEntity<?> fetchAir() {
        airQualityService.fetchAirQuality("PM10");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/air-get")
    public ResponseEntity<?> getAir() {
        airQualityService.getAirQualityValue("seoul", "PM10", "2025042220");
        return ResponseEntity.ok().build();
    }

    // chunk: 5분 38.34초
    // no chunk: 5분 51.18초
    // executor: 44초
    @GetMapping
    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> fetchDailyWeather() {
        Map<Long, SiGunGuRegion> regionMap = regionCacheService.regionCache();
        Map<Long, List<Forecast>> weatherMap = weatherService.fetchAllWeather(regionMap);
        weatherCacheService.updateWeatherCache(weatherMap);
        return ResponseEntity.ok(weatherMap);
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getWeather(@PathVariable Long regionId) {
        List<Forecast> forecasts = weatherCacheService.getForecast(regionId);
        return ResponseEntity.ok(forecasts);
    }

    @Operation(
            summary = "당일 날씨 조회",
            description = "위, 경도를 입력하여 속한 지역의 날씨를 조회 현재 시간대로부터 24시간"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 날씨 정보를 반환함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Forecast.class)
            )
    )
    @GetMapping("/position")
    public ResponseEntity<?> getWeatherByPosition(
            @Parameter(description = "경도 (Longitude)") @RequestParam double lat,
            @Parameter(description = "위도 (Latitude)") @RequestParam double lon
    ) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        List<Forecast> forecasts = weatherCacheService.getForecast(region.id());
        SidoRegionCache sidoRegionCache = regionCacheService.findSidoRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        LocalDateTime roundedTime = TimeUtil.roundToNearestHour(LocalDateTime.now());
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String roundedTimeString = TimeUtil.formatTimeToString(roundedTime, outputFormat);
        Integer airQualityValue = airQualityService.getAirQualityValue(sidoRegionCache.apiKey(), "PM10", roundedTimeString);
        AirQualityDto airDto = airQualityService.buildAirQualityDto(airQualityValue);
        WeatherDto dto = WeatherDto.builder()
                .forecasts(forecasts)
                .airQuality(airDto)
                .build();
        return ResponseEntity.ok(dto);
    }
}
