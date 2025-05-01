package com.boram.look.api.controller;

import com.boram.look.api.dto.weather.AirQualityDto;
import com.boram.look.api.dto.weather.MidTermForecastDto;
import com.boram.look.api.dto.weather.WeatherDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.WeatherFacade;
import com.boram.look.service.weather.air.AirQualityService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import com.boram.look.service.weather.forecast.ForecastService;
import com.boram.look.service.weather.mid.MidForecastService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController {
    private final ForecastService forecastService;
    private final RegionCacheService regionCacheService;
    private final ForecastCacheService forecastCacheService;
    private final WeatherFacade weatherFacade;
    private final MidForecastService midService;


    private final AirQualityService airQualityService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/air")
    @Hidden
    public ResponseEntity<?> fetchAir() {
        airQualityService.fetchAirQuality("PM10");
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/air-get")
    @Hidden
    public ResponseEntity<?> getAir() {
        AirQualityDto airDto = airQualityService.getAirQuality("seoul", "PM10");
        return ResponseEntity.ok(airDto);
    }

    // chunk: 5분 38.34초
    // no chunk: 5분 51.18초
    // executor: 44초
    @GetMapping
    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> fetchDailyWeather() {
        Map<Long, SiGunGuRegion> regionMap = regionCacheService.regionCache();
        Map<Long, List<Forecast>> weatherMap = forecastService.fetchAllWeather(regionMap);
        forecastCacheService.updateForecastCache(weatherMap);
        return ResponseEntity.ok(weatherMap);
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getWeather(@PathVariable Long regionId) {
        List<Forecast> forecasts = forecastCacheService.getForecast(regionId);
        return ResponseEntity.ok(forecasts);
    }

    @Operation(
            summary = "당일 날씨 조회",
            description = "위, 경도를 입력하여 속한 지역의 날씨를 조회 현재 시간대로부터 24시간"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 날씨 정보를 반환함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WeatherDto.class)
            )
    )
    @GetMapping("/position")
    public ResponseEntity<?> getWeatherByPosition(
            @Parameter(description = "위도 (Latitude)", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도 (Longitude)", example = "126.9780") @RequestParam double lon
    ) {
        WeatherDto dto = weatherFacade.getWeather(lat, lon);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "10일 날씨 조회",
            description = "위, 경도를 입력하여 속한 시도 지역의 10일간 날씨를 조회"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 날씨 정보를 반환함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MidTermForecastDto.class)
            )
    )
    @GetMapping("/mid-terms")
    public ResponseEntity<?> getMidTermsForecasts(
            @Parameter(description = "위도 (Latitude)", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도 (Longitude)", example = "126.9780") @RequestParam double lon
    ) {
        SidoRegionCache sido = regionCacheService.findSidoRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);

        List<MidTermForecastDto> dto = midService.getMidTermsWeather(sido);
        return ResponseEntity.ok(dto);
    }
}
