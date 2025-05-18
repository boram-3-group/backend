package com.boram.look.service.weather.forecast;

import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.weather.forecast.entity.Forecast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastCacheService {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final ForecastService forecastService;

    public void initForecastCache(Long regionId) {
        List<Forecast> forecasts = forecastService.find24HourForecasts(regionId);
        if (forecasts.isEmpty()) return;
        List<ForecastDto> dtos = forecasts.stream().map(Forecast::toDto).toList();
        Map<Long, List<ForecastDto>> weatherMap = new HashMap<>();
        weatherMap.put(regionId, dtos);
        this.updateForecastCache(weatherMap);
    }

    public void updateForecastCache(Map<Long, List<ForecastDto>> weatherMap) {
        for (Map.Entry<Long, List<ForecastDto>> entry : weatherMap.entrySet()) {
            // 연계 실패한 날씨 정보는 일단 있는 캐시 사용
            if (entry.getValue().isEmpty()) {
                continue;
            }

            String key = "weather:" + entry.getKey(); // ex) weather:110
            String value = null;
            try {
                value = objectMapper.writeValueAsString(entry.getValue());
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }

            assert value != null;
            redisTemplate.opsForValue().set(key, value, Duration.ofHours(4));
        }
    }

    public void put(String regionId, List<ForecastDto> forecastDtos) {
        String key = "weather:" + regionId; // ex) weather:110
        String value = null;
        try {
            value = objectMapper.writeValueAsString(forecastDtos);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        assert value != null;
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(4));
    }

    public List<ForecastDto> getForecast(Long regionId) {
        String key = "weather:" + regionId;
        String json = redisTemplate.opsForValue().get(key);
        List<ForecastDto> forecastDtos = null;
        try {
            forecastDtos = objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return forecastDtos;
    }
}
