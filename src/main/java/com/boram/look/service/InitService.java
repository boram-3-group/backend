package com.boram.look.service;

import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.air.AirQualityService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import com.boram.look.service.weather.forecast.ForecastService;
import com.boram.look.service.weather.forecast.MidForecastAPIService;
import com.boram.look.service.weather.uv.UvIndexService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {
    private final RegionCacheService regionCacheService;
    private final ForecastService forecastService;
    private final ForecastCacheService forecastCacheService;
    private final AirQualityService airQualityService;
    private final UvIndexService uvIndexService;
    private final MidForecastAPIService midForecastAPIService;

    @PostConstruct
    public void initServer() {
        log.info("init server start.");
        regionCacheService.loadRegionMap();
        Map<Long, SiGunGuRegion> regionMap = regionCacheService.regionCache();
        Map<Long, List<Forecast>> weatherMap = forecastService.fetchAllWeather(regionMap);
        forecastCacheService.updateForecastCache(weatherMap);
        airQualityService.fetchAirQuality("PM10");
        uvIndexService.updateUvIndexCache();
        regionCacheService.sidoCache().forEach((id, sido) -> midForecastAPIService.getMidTemperature(sido));
        regionCacheService.sidoCache().forEach((id, sido) -> midForecastAPIService.getMidForecast(sido));

        log.info("init server end.");
    }

}
