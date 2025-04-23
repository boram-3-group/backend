package com.boram.look.service.weather;

import com.boram.look.api.dto.AirQualityDto;
import com.boram.look.api.dto.WeatherDto;
import com.boram.look.domain.forecast.Forecast;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.global.util.TimeUtil;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.air.AirQualityService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import com.boram.look.service.weather.forecast.ForecastService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherFacade {
    private final RegionCacheService regionCacheService;
    private final ForecastCacheService forecastCacheService;
    private final AirQualityService airQualityService;

    public WeatherDto getWeather(double lat, double lon) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        List<Forecast> forecasts = forecastCacheService.getForecast(region.id());

        SidoRegionCache sidoRegionCache = regionCacheService.findSidoRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        LocalDateTime roundedTime = TimeUtil.roundToNearestHour(LocalDateTime.now());
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String roundedTimeString = TimeUtil.formatTimeToString(roundedTime, outputFormat);
        Integer airQualityValue = airQualityService.getAirQualityValue(sidoRegionCache.apiKey(), "PM10", roundedTimeString);
        AirQualityDto airDto = airQualityService.buildAirQualityDto(airQualityValue);
        return WeatherDto.builder()
                .forecasts(forecasts)
                .airQuality(airDto)
                .build();
    }


}
