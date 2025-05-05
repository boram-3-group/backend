package com.boram.look.service.weather.air;

import com.boram.look.api.dto.weather.AirQualityDto;
import com.boram.look.domain.condition.repository.AirQualityRange;
import com.boram.look.domain.condition.repository.AirQualityRangeRepository;
import com.boram.look.domain.weather.air.AirQualityCache;
import com.boram.look.domain.weather.air.AirQualityFetchFailedEvent;
import com.boram.look.global.ex.APIFetchException;
import com.boram.look.global.util.TimeUtil;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AirQualityService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${weather.service-key}")
    private String serviceKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final AirQualityRangeRepository rangeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void fetchAirQuality(String itemType) {
        log.info("fetch air quality....");
        URI requestUri = this.buildAirQualityRequestUrl(itemType);
        ResponseEntity<?> response = restTemplate.getForEntity(requestUri, String.class);
        JsonNode jsonNode = this.getAirQualityJsonNode(response.getBody().toString());
        List<AirQualityCache> cacheList = this.parseAirQualityItems(jsonNode);
        this.cacheAirQuality(cacheList);
    }

    public void cacheAirQuality(List<AirQualityCache> cacheList) {
        for (AirQualityCache entry : cacheList) {
            String key = String.format("airquality:%s:%s",
                    entry.getItemCode(),
                    entry.getDataTime());

            redisTemplate.opsForValue().set(key, entry, Duration.ofHours(2));
        }
    }

    @Transactional(readOnly = true)
    public AirQualityDto getAirQuality(String apiKey, String itemCode) {
        String dataTimeKey = this.buildDateMinus1HourTimeKey();
        String redisKey = String.format("airquality:%s:%s", itemCode, dataTimeKey);
        log.info("redis key: {}", redisKey);
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        if (cached == null || cached.isEmpty()) {
            String spareTimeKey = this.buildDateMinus2HourTimeKey();
            redisKey = String.format("airquality:%s:%s", itemCode, spareTimeKey);
            log.info("spare redis key: {}", redisKey);
            cached = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        }
        Integer currentValue = Integer.parseInt(cached.get(apiKey).toString());
        AirQualityRange range = rangeRepository.getByCurrentQuality(currentValue).orElseThrow(EntityNotFoundException::new);
        return range.toDto(currentValue);
    }

    private String buildDateMinus1HourTimeKey() {
        LocalDateTime roundedTime = LocalDateTime.now().minusHours(1).withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return TimeUtil.formatTimeToString(roundedTime, outputFormat);
    }

    private String buildDateMinus2HourTimeKey() {
        LocalDateTime roundedTime = LocalDateTime.now().minusHours(2).withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return TimeUtil.formatTimeToString(roundedTime, outputFormat);
    }


    private List<AirQualityCache> parseAirQualityItems(JsonNode jsonNode) {
        JsonNode items = jsonNode.path("response").path("body").path("items");
        List<AirQualityCache> results = new ArrayList<>();
        for (JsonNode item : items) {
            String input = item.get("dataTime").asText();
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
            LocalDateTime dateTime = LocalDateTime.parse(input, inputFormat);
            String formatted = dateTime.format(outputFormat);
            AirQualityCache dto = AirQualityCache.builder()
                    .seoul(item.get("seoul").asInt())
                    .gyeonggi(item.get("gyeonggi").asInt())
                    .incheon(item.get("incheon").asInt())
                    .gangwon(item.get("gangwon").asInt())
                    .daejeon(item.get("daejeon").asInt())
                    .sejong(item.get("sejong").asInt())
                    .chungbuk(item.get("chungbuk").asInt())
                    .chungnam(item.get("chungnam").asInt())
                    .gwangju(item.get("gwangju").asInt())
                    .jeonbuk(item.get("jeonbuk").asInt())
                    .jeonnam(item.get("jeonnam").asInt())
                    .busan(item.get("busan").asInt())
                    .daegu(item.get("daegu").asInt())
                    .ulsan(item.get("ulsan").asInt())
                    .gyeongbuk(item.get("gyeongbuk").asInt())
                    .gyeongnam(item.get("gyeongnam").asInt())
                    .jeju(item.get("jeju").asInt())
                    .dataGubun(item.get("dataGubun").asText())
                    .dataTime(formatted)
                    .itemCode(item.get("itemCode").asText())
                    .build();
            results.add(dto);
        }

        return results;
    }

    private JsonNode getAirQualityJsonNode(String responseBody) {
        try {
            return objectMapper.readTree(responseBody);
        } catch (JsonProcessingException | NullPointerException e) {
            eventPublisher.publishEvent(new AirQualityFetchFailedEvent(this));
            throw new APIFetchException();
        }
    }

    private URI buildAirQualityRequestUrl(String itemType) {
        String requestUrl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureLIst";
        String resultUrl = UriComponentsBuilder.fromUriString(requestUrl)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("returnType", "JSON")
                .queryParam("itemCode", itemType)
                .queryParam("dataGubun", "HOUR")
                .queryParam("searchCondition", "DAY")
                .build(false)
                .toUriString();
        return URI.create(resultUrl);
    }


}
