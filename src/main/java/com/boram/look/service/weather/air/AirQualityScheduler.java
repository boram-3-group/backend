package com.boram.look.service.weather.air;

import com.boram.look.domain.weather.air.AirQualityFetchFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AirQualityScheduler {

    private final AirQualityService airQualityService;
    private volatile boolean isFailed = true;

    @EventListener
    public void onFetchFailed(AirQualityFetchFailedEvent event) {
        log.warn("[AirQuality] Fetch failed. Attempting immediate retry...");
        try {
            airQualityService.fetchAirQuality("PM10");
            log.info("[AirQuality] Immediate retry successful.");
            // 즉시 재시도 성공했으면 실패 상태 해제
            isFailed = false;
        } catch (Exception e) {
            log.error("[AirQuality] Immediate retry failed. Will retry later.", e);
            // 즉시 재시도도 실패하면 실패 상태로 유지
            isFailed = true;
        }
    }

    @Scheduled(cron = "0 30 * * * *")
    public void retryFetchIfNeeded() {
        log.info("whether air quality fetch is needed: {}", this.isFailed);
        if (isFailed) {
            log.info("[AirQuality] Scheduled retry started.");
            try {
                airQualityService.fetchAirQuality("PM10");
                log.info("[AirQuality] Scheduled retry successful.");
                // 성공하면 실패 상태 해제
                isFailed = false;
            } catch (Exception e) {
                // 실패했으면 계속 failed = true
                log.error("[AirQuality] Scheduled retry failed.", e);
            }
        }
    }

    @Scheduled(cron = "0 10 * * * *")
    public void runAirQualityBatch() {
        log.info("run air quality batch.");
        log.info("[AirQuality] Scheduled retry started.");
        try {
            airQualityService.fetchAirQuality("PM10");
            log.info("[AirQuality] Scheduled retry successful.");
            // 성공하면 실패 상태 해제
            isFailed = false;
        } catch (Exception e) {
            // 실패했으면 계속 failed = true
            log.error("[AirQuality] Scheduled retry failed.", e);
        }
    }
}
