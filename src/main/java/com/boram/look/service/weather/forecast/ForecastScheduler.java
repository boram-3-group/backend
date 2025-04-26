package com.boram.look.service.weather.forecast;

import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.domain.weather.forecast.entity.ForecastFetchFailure;
import com.boram.look.service.region.RegionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastScheduler {
    private final ForecastFailureService forecastFailureService;
    private final ForecastCacheService forecastCacheService;
    private final RegionCacheService regionCacheService;
    private final ForecastService forecastService;

    @Scheduled(cron = "0 15 2,5,8,11,14,17,20,23 * * *") // 매일 02:15, 05:15, ... 실행
    public void runForecastBatch() {
        Map<Long, List<Forecast>> weatherMap = forecastService.fetchAllWeather(regionCacheService.regionCache());
        forecastCacheService.updateForecastCache(weatherMap);
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // 10분마다
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

            List<Forecast> forecasts = forecastService.fetchWeatherForRegion(region.grid().nx(), region.grid().ny(), region.id());
            // forecasts가 빈 리스트이면 연계가 실패한 것으로 간주
            if (forecasts.isEmpty()) {
                forecastFailureService.updateFailureTime(failure.getRegionId());
                continue;
            }

            forecastCacheService.put(regionId.toString(), forecasts);
            forecastFailureService.removeFailure(regionId); // 성공 시 삭제
        }
    }

}
