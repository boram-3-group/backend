package com.boram.look.service.weather;

import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.domain.weather.entity.WeatherFetchFailure;
import com.boram.look.service.region.RegionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherScheduler {
    private final WeatherFailureService weatherFailureService;
    private final WeatherCacheService weatherCacheService;
    private final RegionCacheService regionCacheService;
    private final WeatherService weatherService;

    @Scheduled(cron = "0 15 2,5,8,11,14,17,20,23 * * *") // 매일 02:15, 05:15, ... 실행
    public void runForecastBatch() {
        weatherService.fetchAllWeather(regionCacheService.cache());
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // 10분마다
    public void retryFailedRegions() {
        List<WeatherFetchFailure> failures = weatherFailureService.findAllFailures();
        if (failures.isEmpty()) {
            return;
        }

        for (WeatherFetchFailure failure : failures) {
            Long regionId = failure.getRegionId();
            SiGunGuRegion region = regionCacheService.cache().get(regionId);
            if (region == null) {
                log.warn("캐시에 regionId={} 정보 없음", regionId);
                continue;
            }

            List<Forecast> forecasts = weatherService.fetchWeatherForRegion(region.grid().nx(), region.grid().ny(), region.id());
            // forecasts가 빈 리스트이면 연계가 실패한 것으로 간주
            if (forecasts.isEmpty()) {
                weatherFailureService.updateFailureTime(failure.getRegionId());
                continue;
            }

            weatherCacheService.put(regionId.toString(), forecasts);
            weatherFailureService.removeFailure(regionId); // 성공 시 삭제
        }
    }

}
