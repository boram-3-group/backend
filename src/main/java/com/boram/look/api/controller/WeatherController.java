package com.boram.look.api.controller;

import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.WeatherCacheService;
import com.boram.look.service.weather.WeatherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController {
    private final WeatherService weatherService;
    private final RegionCacheService regionCacheService;
    private final WeatherCacheService weatherCacheService;

    // chunk: 5분 38.34초
    // no chunk: 5분 51.18초
    // executor: 44초
    @GetMapping
    public ResponseEntity<?> fetchDailyWeather() {
        Map<Long, SiGunGuRegion> regionMap = regionCacheService.cache();
        Map<Long, List<Forecast>> weatherMap = weatherService.fetchAllWeather(regionMap);
        weatherCacheService.updateWeatherCache(weatherMap);
        return ResponseEntity.ok(weatherMap);
    }


    @GetMapping("/region/{regionId}")
    public ResponseEntity<?> getWeather(@PathVariable Long regionId) {
        List<Forecast> forecasts = weatherCacheService.getForecast(regionId);
        return ResponseEntity.ok(forecasts);
    }

    @GetMapping("/position")
    public ResponseEntity<?> getWeatherByPosition(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        List<Forecast> forecasts = weatherCacheService.getForecast(region.id());
        return ResponseEntity.ok(forecasts);
    }
}
