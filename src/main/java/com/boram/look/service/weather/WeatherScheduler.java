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
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherScheduler {
    private final WeatherFailureService weatherFailureService;
    private final WeatherCacheService weatherCacheService;
    private final RegionCacheService regionCacheService;
    private final WeatherService weatherService;

    @Scheduled(cron = "20 10,13,16,19,22,1,4 * * *") // 매일 02:15, 05:15, ... 실행
    public void runForecastBatch() throws ExecutionException, InterruptedException {
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

            try {
                List<Forecast> forecasts = weatherService.fetchWeatherForRegion(region.grid().nx(), region.grid().ny(), region.id());
                weatherCacheService.put(regionId.toString(), forecasts);
                weatherFailureService.removeFailure(regionId); // 성공 시 삭제
            } catch (Exception e) {
                log.warn("재시도 실패: regionId={}, reason={}", regionId, e.getMessage());
            }
        }
    }

}
