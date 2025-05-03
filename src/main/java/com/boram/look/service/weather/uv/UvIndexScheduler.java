package com.boram.look.service.weather.uv;

import com.boram.look.domain.weather.uv.entity.UvFetchFailure;
import com.boram.look.domain.weather.uv.event.UvIndexFetchFailedEvent;
import com.boram.look.service.region.RegionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UvIndexScheduler {

    private final UvIndexService uvIndexService;
    private final RegionCacheService regionCacheService;

    @EventListener
    public void onFetchFailed(UvIndexFetchFailedEvent event) {
        log.warn("[UvIndex] Fetch failed. Attempting immediate retry...");
        try {
            // 즉시 재시도 시작
            uvIndexService.fetchUvIndexAsync(event.getSido());
            log.info("[UvIndex] Immediate retry successful.");
        } catch (Exception e) {
            // 즉시 재시도 실패시 실패 항목 저장
            uvIndexService.saveFailure(event.getSido());
            log.error("[UvIndex] Immediate retry failed. Will retry later.", e);
        }
    }

    @Scheduled(cron = "0 10 * * * *")
    public void retryFetchIfNeeded() {
        List<UvFetchFailure> failures = uvIndexService.findAllFailure();
        if (failures.isEmpty()) {
            log.info("[UvIndex] All Fetches are already done.");
            return;
        }

        failures.forEach(failure -> uvIndexService.fetchUvIndexAsync(failure.getSido()));
    }

    @Scheduled(cron = "0 10 0,3,6,9,12,15,18,21 * * *")
    public void runUvIndexBatch() {
        regionCacheService.sidoCache().forEach((id, sido) -> uvIndexService.fetchUvIndexAsync(sido.sido()));
    }

}
