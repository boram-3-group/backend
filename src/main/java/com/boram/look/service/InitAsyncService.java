package com.boram.look.service;

import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.weather.forecast.entity.Forecast;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.air.AirQualityService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import com.boram.look.service.weather.forecast.ForecastAPIService;
import com.boram.look.service.weather.forecast.ForecastService;
import com.boram.look.service.weather.mid.MidForecastAPIService;
import com.boram.look.service.weather.uv.UvIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitAsyncService {
    private final RegionCacheService regionCacheService;
    private final ForecastAPIService forecastAPIService;
    private final ForecastCacheService forecastCacheService;
    private final AirQualityService airQualityService;
    private final UvIndexService uvIndexService;
    private final MidForecastAPIService midForecastAPIService;
    private final ForecastService forecastService;

    @Async
    public void asyncInit() {
        Map<Long, SiGunGuRegion> regionMap = regionCacheService.regionCache();
        Map<Long, List<ForecastDto>> weatherMap = forecastAPIService.fetchAllWeather(regionMap);
        forecastService.saveShortTermsForecast(weatherMap);
        regionMap.forEach((regionId, regionCache) -> forecastCacheService.initForecastCache(regionId));
        airQualityService.fetchAirQuality("PM10");
        uvIndexService.updateUvIndexCache();

        log.info("fetch mid temperature....");
        regionCacheService.sidoCache().forEach((id, sido) -> midForecastAPIService.fetchMidTemperature(sido));
        log.info("fetch mid forecast....");
        regionCacheService.sidoCache().forEach((id, sido) -> midForecastAPIService.fetchMidForecast(sido));
    }

}
