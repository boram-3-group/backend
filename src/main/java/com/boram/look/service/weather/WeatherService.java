package com.boram.look.service.weather;

import com.boram.look.api.dto.WeatherForecastDto;
import com.boram.look.common.constants.WeatherConstants;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.domain.weather.ForecastBase;
import com.boram.look.domain.weather.ForecastMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final WeatherFailureService failureService;
    private final ObjectMapper objectMapper;

    @Value("${weather.vilage-fcst-url}")
    private String vilageFcstUrl;
    @Value("${weather.service-key}")
    private String serviceKey;

    public List<WeatherForecastDto> callWeather(int nx, int ny, Long regionId) {
        ForecastBase base = this.getNearestForecastBase();
        String url = this.buildWeatherRequestUrl(base, nx, ny);

        JsonNode root = this.getWeatherJsonNode(url, regionId);
        if (root == null) {
            return Collections.emptyList();
        }

        return this.parseWeatherItems(root);
    }

    private JsonNode getWeatherJsonNode(String url, Long regionId) {
        URI uri = URI.create(url);
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.error("fail region id: {}\nbody: {}", regionId, response.getBody());
            failureService.saveFailure(regionId);
            return null;
        }
    }

    private String buildWeatherRequestUrl(ForecastBase base, int nx, int ny) {
        return UriComponentsBuilder.fromUriString(this.vilageFcstUrl)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
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
                    .build();
            results.add(dto);
        }

        return results;
    }


    public List<Forecast> fetchWeatherForRegion(int nx, int ny, Long regionId) {
        List<WeatherForecastDto> res = this.callWeather(nx, ny, regionId);
        return this.mergeForecasts(res);
    }

    public List<Forecast> mergeForecasts(List<WeatherForecastDto> rawItems) {
        Map<String, Forecast> timeMap = new TreeMap<>();

        for (WeatherForecastDto item : rawItems) {
            Forecast forecast = timeMap.computeIfAbsent(item.fcstTime(), t -> {
                Forecast f = new Forecast();
                f.setTime(t);
                return f;
            });
            ForecastMapper.apply(forecast, item);
        }

        for (Forecast f : timeMap.values()) {
            f.setIcon(ForecastMapper.getWeatherIcon(f.getPty(), f.getSky()));
            f.setMessage(ForecastMapper.getMessage(f.getPty(), f.getSky()));
        }

        return new ArrayList<>(timeMap.values());
    }

    public ForecastBase getNearestForecastBase() {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

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
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Semaphore limiter = new Semaphore(30);
        Map<Long, List<Forecast>> weatherMap = new ConcurrentHashMap<>();
        AtomicInteger count = new AtomicInteger();

        List<Future<Void>> futures = regionMap.entrySet().stream()
                .map(entry -> executor.submit(((Callable<Void>) () -> {
                    runWithThrottle(entry.getKey(), entry.getValue(), weatherMap, limiter, count);
                    return null;
                }))).toList();

        for (Future<Void> future : futures) {
            try {
                future.get(); // 예외 발생 시 throw 됨
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
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

            List<Forecast> forecasts = this.fetchWeatherForRegion(
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
