package com.boram.look.service.weather.uv;

import com.boram.look.api.dto.weather.UvIndexDto;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.air.AirQualityFetchFailedEvent;
import com.boram.look.domain.weather.uv.UvIndexCache;
import com.boram.look.domain.weather.uv.UvIndexRange;
import com.boram.look.domain.weather.uv.entity.UvFetchFailure;
import com.boram.look.domain.weather.uv.event.UvIndexFetchFailedEvent;
import com.boram.look.domain.weather.uv.repository.UvFetchFailureRepository;
import com.boram.look.domain.weather.uv.repository.UvRangeRepository;
import com.boram.look.global.util.TimeUtil;
import com.boram.look.service.region.RegionCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class UvIndexService {
    private final RegionCacheService regionCacheService;
    private final UvFetchFailureRepository failureRepository;
    private final UvRangeRepository rangeRepository;

    @Value("${weather.service-key}")
    private String serviceKey;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public void updateUvIndexCache() {
        log.info("update uv index cache....");
        Map<Long, SidoRegionCache> regionMap = regionCacheService.sidoCache();
        regionMap.forEach((key, value) -> this.fetchUvIndexAsync(value.sido()));
    }

    @Async
    public void fetchUvIndexAsync(String sido) {
        fetchUvIndex(sido);
        CompletableFuture.completedFuture(null);
    }

    public UvIndexDto getUvIndex(String sido) {
        String dateTimeKey = TimeUtil.getNearestPastThreeHour(LocalDateTime.now());
        String redisKey = String.format("uvindex:%s:%s", sido, dateTimeKey);
        log.info("redis key: {}", redisKey);
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        Integer currentValue = Integer.parseInt(cached.get("h0").toString());
        UvIndexRange range = rangeRepository.getByCurrentQuality(currentValue).orElseThrow(EntityNotFoundException::new);
        return range.toDto(currentValue);
    }

    @Transactional
    public void saveFailure(String sido) {
        failureRepository.save(UvFetchFailure.builder()
                .sido(sido)
                .build());
    }

    @Transactional(readOnly = true)
    public List<UvFetchFailure> findAllFailure() {
        return failureRepository.findAll();
    }

    private void fetchUvIndex(String sido) {
        String areaNo = sido + "00000000";
        String time = TimeUtil.getNearestFetchThreeHour(LocalDateTime.now());
        log.info("uv index fetch time: {}", time);
        URI requestUri = this.buildUvIndexRequestUrl(areaNo, time);
        try {

            ResponseEntity<?> response = restTemplate.getForEntity(requestUri, String.class);

            JsonNode jsonNode = this.getUvIndexJsonNode(response.getBody().toString(), sido);
            if (jsonNode == null) {
                return;
            }
            UvIndexCache uvIndex = this.parseUvIndexItems(jsonNode, sido, time);
            this.saveUvIndex(uvIndex, time);
        } catch (JsonProcessingException | ResourceAccessException e) {
            eventPublisher.publishEvent(new UvIndexFetchFailedEvent(this, sido));
        }
    }

    private void saveUvIndex(UvIndexCache uvIndex, String time) {
        if (uvIndex == null || uvIndex.sido() == null || time == null) {
            eventPublisher.publishEvent(new AirQualityFetchFailedEvent(this));
            return;
        }

        String key = String.format("uvindex:%s:%s", uvIndex.sido(), time);
        redisTemplate.opsForValue().set(key, uvIndex, Duration.ofHours(4));
    }

    private UvIndexCache parseUvIndexItems(JsonNode jsonNode, String sido, String time) {
        JsonNode items = jsonNode.path("response").path("body").path("items").path("item").get(0);
        return UvIndexCache.builder()
                .h0(Integer.parseInt(items.get("h0").asText()))
                .h3(Integer.parseInt(items.get("h3").asText()))
                .h6(Integer.parseInt(items.get("h6").asText()))
                .fetchedTime(time)
                .sido(sido)
                .build();
    }

    private JsonNode getUvIndexJsonNode(String response, String sido) throws JsonProcessingException {
        return objectMapper.readTree(response);
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


