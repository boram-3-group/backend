package com.boram.look.service;

import com.boram.look.service.region.RegionCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {
    private final RegionCacheService regionCacheService;
    private final InitAsyncService asyncService;

    @PostConstruct
    public void initServer() {
        log.info("init server start.");
        regionCacheService.loadRegionMap();
        asyncService.asyncInit();
        log.info("init server end.");
    }

}
