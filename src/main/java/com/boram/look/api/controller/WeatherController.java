package com.boram.look.api.controller;

import com.boram.look.api.dto.WeatherResponse;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherController {
    private final WeatherService weatherService;
    private final RegionCacheService regionCacheService;

    @GetMapping
    public ResponseEntity<?> fetchDailyWeather() {
        Map<Long, SiGunGuRegion> regionMap = regionCacheService.cache();
        Map<Long, List<Forecast>> weatherMap = new HashMap<>();
        for (Map.Entry<Long, SiGunGuRegion> entry : regionMap.entrySet()) {
            SiGunGuRegion region = entry.getValue();
            List<Forecast> forecasts = weatherService.fetchWeatherForRegion(region.grid().nx(), region.grid().ny());
            weatherMap.put(entry.getKey(), forecasts);
        }
        return ResponseEntity.ok(weatherMap);
    }
}
