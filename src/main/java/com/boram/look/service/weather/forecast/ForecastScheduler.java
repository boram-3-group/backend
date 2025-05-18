package com.boram.look.service.weather.forecast;

import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.weather.forecast.ForecastBase;
import com.boram.look.domain.weather.forecast.entity.ForecastFetchFailure;
import com.boram.look.service.region.RegionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastScheduler {
    private final ForecastFailureService forecastFailureService;
    private final ForecastCacheService forecastCacheService;
    private final RegionCacheService regionCacheService;
    private final ForecastAPIService forecastAPIService;
    private final ForecastService forecastService;

    @Scheduled(cron = "0 15 2,5,8,11,14,17,20,23 * * *") // 매일 02:15, 05:15, ... 실행
    public void runForecastBatch() {
        Map<Long, List<ForecastDto>> weatherMap = forecastAPIService.fetchAllWeather(regionCacheService.regionCache());
        Map<Long, List<ForecastDto>> dailyMap = forecastService.saveShortTermsForecast(weatherMap);
        forecastCacheService.updateForecastCache(dailyMap);
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000) // 60분마다
    public void retryFailedRegions() {
        List<ForecastFetchFailure> failures = forecastFailureService.findAllFailures();
        if (failures.isEmpty()) {
            return;
        }

        for (ForecastFetchFailure failure : failures) {
            Long regionId = failure.getRegionId();
            SiGunGuRegion region = regionCacheService.regionCache().get(regionId);
            if (region == null) {
                log.warn("캐시에 regionId={} 정보 없음", regionId);
                continue;
            }

            ForecastBase base = forecastAPIService.getNearestForecastBase(LocalDate.now(), LocalTime.now());
            List<ForecastDto> forecastDtos = forecastAPIService.fetchWeatherForRegion(base, region.grid().nx(), region.grid().ny(), region.id());
            // forecasts가 빈 리스트이면 연계가 실패한 것으로 간주
            if (forecastDtos.isEmpty()) {
                forecastFailureService.updateFailureTime(failure.getRegionId());
                continue;
            }

            List<ForecastDto> dailyList = forecastService.saveShortTermsForecastByRegion(forecastDtos, regionId);
            if (dailyList.isEmpty()) return;
            forecastCacheService.put(regionId.toString(), dailyList);
            forecastFailureService.removeFailure(regionId); // 성공 시 삭제
        }
    }

}
