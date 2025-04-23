package com.boram.look.service.air;

import com.boram.look.api.dto.AirQualityDto;
import com.boram.look.domain.air.AirQualityCache;
import com.boram.look.global.ex.APIFetchException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public void fetchAirQuality(String itemType) {
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

    //TODO: 메세지와 아이콘 매핑하기
    public AirQualityDto buildAirQualityDto(Integer airQualityValue) {
        return AirQualityDto.builder()
                .airQuality(airQualityValue)
                .message("asd")
                .iconUrl("asd")
                .build();
    }

    public Integer getAirQualityValue(String apiKey, String itemCode, String dataTimeKey) {
        String redisKey = String.format("airquality:%s:%s", itemCode, dataTimeKey);
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        if (cached == null) return null;
        Object value = cached.get(apiKey);
        return value != null ? Integer.parseInt(value.toString()) : null;
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
