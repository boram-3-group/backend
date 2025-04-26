package com.boram.look.service.weather.uv;

import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.air.AirQualityFetchFailedEvent;
import com.boram.look.domain.weather.uv.UvIndexCache;
import com.boram.look.global.util.TimeUtil;
import com.boram.look.service.region.RegionCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class UvIndexService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RegionCacheService regionCacheService;

    @Value("${weather.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public void updateUvIndexCache() {
        Map<Long, SidoRegionCache> regionMap = regionCacheService.sidoCache();
        regionMap.entrySet().forEach(this::fetchUvIndexAsync);
    }

    @Async
    public void fetchUvIndexAsync(Map.Entry<Long, SidoRegionCache> entry) {
        fetchUvIndex(entry);
        CompletableFuture.completedFuture(null);
    }

    private void fetchUvIndex(Map.Entry<Long, SidoRegionCache> entry) {
        SidoRegionCache cache = entry.getValue();
        String areaNo = cache.sido() + "00000000";
        String time = TimeUtil.getNearestPastThreeHour(LocalDateTime.now());
        URI requestUri = this.buildUvIndexRequestUrl(areaNo, time);
        ResponseEntity<?> response = restTemplate.getForEntity(requestUri, String.class);
        JsonNode jsonNode = this.getUvIndexJsonNode(response.getBody().toString());
        if (jsonNode == null) {
            return;
        }
        UvIndexCache uvIndex = this.parseUvIndexItems(jsonNode, cache.sido());
        this.saveUvIndex(uvIndex, time);
    }

    private void saveUvIndex(UvIndexCache uvIndex, String time) {
        if (uvIndex == null || uvIndex.sido() == null || time == null) {
            eventPublisher.publishEvent(new AirQualityFetchFailedEvent(this));
            return;
        }

        String key = String.format("uvindex:%s%s", uvIndex.sido(), time);
        redisTemplate.opsForValue().set(key, uvIndex, Duration.ofHours(4));
    }

    private UvIndexCache parseUvIndexItems(JsonNode jsonNode, String sido) {
        JsonNode items = jsonNode.path("response").path("body").path("items");
        return UvIndexCache.builder()
                .h0(items.get("h0").asInt())
                .h3(items.get("h3").asInt())
                .h6(items.get("h6").asInt())
                .fetchedTime(LocalDateTime.now())
                .sido(sido)
                .build();
    }

    private JsonNode getUvIndexJsonNode(String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            eventPublisher.publishEvent(new AirQualityFetchFailedEvent(this));
            return null;
        }
    }

    private URI buildUvIndexRequestUrl(String areaNo, String time) {
        String requestUrl = "https://apis.data.go.kr/1360000/LivingWthrIdxServiceV4/getUVIdxV4";
        String resultUrl = UriComponentsBuilder.fromUriString(requestUrl)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("areaNo", areaNo)
                .queryParam("time", time)
                .build(false)
                .toUriString();
        return URI.create(resultUrl);
    }
}


