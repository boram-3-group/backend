package com.boram.look.service.weather;

import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.weather.Forecast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherCacheService {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public void updateWeatherCache(Map<Long, List<Forecast>> weatherMap) {
        for (Map.Entry<Long, List<Forecast>> entry : weatherMap.entrySet()) {
            // 연계 실패한 날씨 정보는 일단 있는 캐시 사용
            if (entry.getValue().isEmpty()) {
                continue;
            }

            String key = "weather:" + entry.getKey(); // ex) weather:110
            String value = null;
            try {
                value = objectMapper.writeValueAsString(entry.getValue());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            assert value != null;
            redisTemplate.opsForValue().set(key, value, Duration.ofHours(4));
        }
    }

    public void put(String regionId, List<Forecast> forecasts) {
        String value = null;
        try {
            value = objectMapper.writeValueAsString(forecasts);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert value != null;
        redisTemplate.opsForValue().set(regionId, value, Duration.ofHours(4));
    }

    public List<Forecast> getForecast(Long regionId) {
        String key = "weather:" + regionId;
        String json = redisTemplate.opsForValue().get(key);
        List<Forecast> forecasts = null;
        try {
            forecasts = objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
        }
        return forecasts;
    }
}
