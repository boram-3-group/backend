package com.boram.look.service.weather;

import com.boram.look.api.dto.weather.AirQualityDto;
import com.boram.look.api.dto.weather.UvIndexDto;
import com.boram.look.api.dto.weather.WeatherDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.air.AirQualityService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import com.boram.look.service.weather.uv.UvIndexService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherFacade {
    private final RegionCacheService regionCacheService;
    private final ForecastCacheService forecastCacheService;
    private final AirQualityService airQualityService;
    private final UvIndexService uvIndexService;

    public WeatherDto getWeather(double lat, double lon) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        List<Forecast> forecasts = forecastCacheService.getForecast(region.id());

        SidoRegionCache sidoRegionCache = regionCacheService.findSidoRegionByLocation(lat, lon)
                .orElseThrow(EntityNotFoundException::new);
        AirQualityDto airDto = airQualityService.getAirQuality(sidoRegionCache.apiKey(), "PM10");
        UvIndexDto uvIndexDto = uvIndexService.getUvIndex(sidoRegionCache.sido());
        WeatherDto dto = WeatherDto.builder()
                .forecasts(forecasts)
                .airQuality(airDto)
                .uvIndex(uvIndexDto)
                .build();
        dto.buildWeatherMessage();
        return dto;
    }


}
