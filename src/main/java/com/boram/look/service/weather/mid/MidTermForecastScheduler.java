package com.boram.look.service.weather.mid;

import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.mid.MidTermFailure;
import com.boram.look.service.region.RegionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidTermForecastScheduler {
    private final MidForecastAPIService midForecastAPIService;
    private final MidTermFailureService failureService;
    private final MidForecastService midForecastService;
    private final RegionCacheService regionCacheService;

    @Scheduled(cron = "0 30 6 * * *")
    public void loadMidTermWeather() {
        regionCacheService.sidoCache().forEach((id, sido) -> midForecastAPIService.fetchMidTemperature(sido));
        regionCacheService.sidoCache().forEach((id, sido) -> midForecastAPIService.fetchMidForecast(sido));
        midForecastService.deletePastDateWeather();
    }

    @Scheduled(cron = "0 30 * * * *")
    public void loadFailureMidTermWeather() {
        List<MidTermFailure> failures = failureService.getAllFailures();
        if (failures.isEmpty()) {
            log.info("all mid forecasts are already fetched.");
            return;
        }

        for (MidTermFailure failure : failures) {
            SidoRegionCache sido = regionCacheService.sidoCache().get(failure.getSidoId());
            midForecastAPIService.fetchMidTemperature(sido);
            midForecastAPIService.fetchMidForecast(sido);
        }
    }

}
