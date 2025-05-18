package com.boram.look.api.controller;

import com.boram.look.api.dto.weather.AirQualityDto;
import com.boram.look.api.dto.weather.MidTermForecastDto;
import com.boram.look.api.dto.weather.WeatherDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.weather.forecast.ForecastBase;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.WeatherFacade;
import com.boram.look.service.weather.air.AirQualityService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import com.boram.look.service.weather.forecast.ForecastAPIService;
import com.boram.look.service.weather.forecast.ForecastFailureService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
@Slf4j
public class WeatherController {
    private final ForecastAPIService forecastAPIService;
    private final RegionCacheService regionCacheService;
    private final ForecastCacheService forecastCacheService;
    private final ForecastService forecastService;
    private final WeatherFacade weatherFacade;
    private final MidForecastService midService;
    private final ForecastFailureService forecastFailureService;

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
//        Map<Long, SiGunGuRegion> regionMap = regionCacheService.regionCache();
//        Map<Long, List<ForecastDto>> weatherMap = forecastAPIService.fetchAllWeather(regionMap);
//        Map<Long, List<ForecastDto>> dailyMap = forecastService.saveShortTermsForecast(weatherMap);
//        forecastCacheService.updateForecastCache(dailyMap);

        regionCacheService.regionCache().forEach(((regionId, region) -> {
            if (region == null) {
                log.warn("캐시에 regionId={} 정보 없음", regionId);
                return;
            }

            ForecastBase base = forecastAPIService.getNearestForecastBase(LocalDate.now(), LocalTime.now());
            List<ForecastDto> forecastDtos = forecastAPIService.fetchWeatherForRegion(base, region.grid().nx(), region.grid().ny(), region.id());
            // forecasts가 빈 리스트이면 연계가 실패한 것으로 간주
            if (forecastDtos.isEmpty()) {
                forecastFailureService.updateFailureTime(regionId);
                return;
            }

            List<ForecastDto> dailyList = forecastService.saveShortTermsForecastByRegion(forecastDtos, regionId);
            if (dailyList.isEmpty()) return;
            forecastCacheService.put(regionId.toString(), dailyList);


            try {
                Thread.sleep(300); // optional: 외부 API 과부하 방지용
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

        return ResponseEntity.ok().build();
    }

    @Hidden
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getWeather(@PathVariable Long regionId) {
        List<ForecastDto> forecastDtos = forecastCacheService.getForecast(regionId);
        return ResponseEntity.ok(forecastDtos);
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
