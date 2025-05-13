package com.boram.look.service.weather.forecast;

import com.boram.look.api.dto.weather.WeatherForecastDto;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.global.constant.WeatherConstants;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.domain.weather.forecast.ForecastBase;
import com.boram.look.domain.weather.forecast.ForecastMapper;
import com.boram.look.domain.weather.forecast.entity.ForecastIcon;
import com.boram.look.global.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastService {
    private final RestTemplate restTemplate;
    private final ForecastFailureService failureService;
    private final ObjectMapper objectMapper;

    @Value("${weather.vilage-fcst-url}")
    private String vilageFcstUrl;
    @Value("${weather.service-key}")
    private String serviceKey;

    public List<WeatherForecastDto> callWeather(ForecastBase base, int nx, int ny, Long regionId) {
        String url = this.buildWeatherRequestUrl(base, nx, ny);

        try {
            URI uri = URI.create(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            if (root == null) {
                return Collections.emptyList();
            }

            return this.parseWeatherItems(root);
        } catch (JsonProcessingException | ResourceAccessException e) {
            log.error("fail region id: {}", regionId);
            failureService.saveFailure(regionId);
            return null;
        }
    }

    private String buildWeatherRequestUrl(ForecastBase base, int nx, int ny) {
        return UriComponentsBuilder.fromUriString(this.vilageFcstUrl)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 290)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", base.baseDate())
                .queryParam("base_time", base.baseTime()) // 0200, 0500 ...
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(false)
                .toUriString();
    }

    private List<WeatherForecastDto> parseWeatherItems(JsonNode root) {
        JsonNode items = root.path("response").path("body").path("items").path("item");

        List<WeatherForecastDto> results = new ArrayList<>();
        for (JsonNode item : items) {
            WeatherForecastDto dto = WeatherForecastDto.builder()
                    .fcstTime(item.get("fcstTime").asText())
                    .category(item.get("category").asText())
                    .fcstValue(item.get("fcstValue").asText())
                    .fcstDate(item.get("fcstDate").asText())
                    .build();
            results.add(dto);
        }

        return results;
    }


    public List<Forecast> fetchWeatherForRegion(ForecastBase base, int nx, int ny, Long regionId) {
        List<WeatherForecastDto> res = this.callWeather(base, nx, ny, regionId);
        if (res == null || res.isEmpty()) {
            return Collections.emptyList();
        }
        return this.mergeForecasts(res);
    }

    public List<Forecast> mergeForecasts(List<WeatherForecastDto> rawItems) {
        Map<String, Forecast> timeMap = new TreeMap<>();

        for (WeatherForecastDto item : rawItems) {
            String dateTimeKey = item.fcstDate() + item.fcstTime();
            Forecast forecast = timeMap.computeIfAbsent(dateTimeKey, t -> {
                Forecast f = new Forecast();
                f.setTime(item.fcstTime());
                f.setDate(item.fcstDate());
                return f;
            });
            ForecastMapper.apply(forecast, item);
        }

        for (Forecast f : timeMap.values()) {
            ForecastIcon icon = ForecastMapper.getWeatherIcon(f.getTime(), f.getPty(), f.getSky());
            f.withForecastIcon(icon);
        }

        return new ArrayList<>(timeMap.values());
    }

    public ForecastBase getNearestForecastBase(LocalDate today, LocalTime now) {
        for (int i = WeatherConstants.BASE_TIME_LIST.size() - 1; i >= 0; i--) {
            String time = WeatherConstants.BASE_TIME_LIST.get(i);
            LocalTime baseTime = LocalTime.parse(time, WeatherConstants.TIME_FORMATTER);

            if (now.isAfter(baseTime) || now.equals(baseTime)) {
                return new ForecastBase(today.format(WeatherConstants.DATE_FORMATTER), time);
            }
        }

        // 자정 직후 → 전날 23시 예보
        return new ForecastBase(today.minusDays(1).format(WeatherConstants.DATE_FORMATTER), "2300");
    }

    public Map<Long, List<Forecast>> fetchAllWeather(Map<Long, SiGunGuRegion> regionMap) {
        log.info("fetch all forecast....");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Semaphore limiter = new Semaphore(30);
        Map<Long, List<Forecast>> weatherMap = new ConcurrentHashMap<>();
        AtomicInteger count = new AtomicInteger();

        List<Future<Void>> futures = regionMap.entrySet().stream()
                .map(entry -> executor.submit(((Callable<Void>) () -> {
                    runWithThrottle(
                            entry.getKey(),
                            entry.getValue(),
                            weatherMap,
                            limiter,
                            count
                    );
                    return null;
                }))).toList();

        for (Future<Void> future : futures) {
            try {
                future.get(); // 예외 발생 시 throw 됨
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        }
        executor.shutdown();
        executor.close();
        return weatherMap;
    }

    private void runWithThrottle(
            Long id,
            SiGunGuRegion region,
            Map<Long, List<Forecast>> weatherMap,
            Semaphore limiter,
            AtomicInteger count
    ) {
        try {
            limiter.acquire();

            int current = count.incrementAndGet();
            if (current % 30 == 0) {
                Thread.sleep(1000); // TPS 제한
            }

            ForecastBase base = this.getNearestForecastBase(LocalDate.now(), LocalTime.now());
            List<Forecast> forecasts = this.fetchWeatherForRegion(
                    base,
                    region.grid().nx(),
                    region.grid().ny(),
                    region.id()
            );
            weatherMap.put(id, forecasts);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 권장되는 처리
        } finally {
            limiter.release();
        }
    }

}
