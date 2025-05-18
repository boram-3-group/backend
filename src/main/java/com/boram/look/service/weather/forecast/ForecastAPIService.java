package com.boram.look.service.weather.forecast;

import com.boram.look.api.dto.weather.WeatherForecastDto;
import com.boram.look.global.constant.WeatherConstants;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.weather.forecast.ForecastBase;
import com.boram.look.domain.weather.forecast.ForecastMapper;
import com.boram.look.domain.weather.forecast.entity.ForecastIcon;
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
public class ForecastAPIService {
    private final RestTemplate restTemplate;
    private final ForecastFailureService failureService;
    private final ObjectMapper objectMapper;

    private final BlockingQueue<Long> queue = new LinkedBlockingQueue<>();
    private final Map<Long, List<ForecastDto>> weatherMap = new ConcurrentHashMap<>();
    private final int CONSUMER_COUNT = 3;
    private final int REQUEST_INTERVAL_MS = 300; // 300ms = 초당 3건

    @Value("${weather.vilage-fcst-url}")
    private String vilageFcstUrl;
    @Value("${weather.service-key}")
    private String serviceKey;

    public List<WeatherForecastDto> callWeather(ForecastBase base, int nx, int ny, Long regionId) {
        String url = this.buildWeatherRequestUrl(base, nx, ny);
        ResponseEntity<String> response = null;
        try {
            URI uri = URI.create(url);
            response = restTemplate.getForEntity(uri, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            if (root == null) {
                return Collections.emptyList();
            }

            return this.parseWeatherItems(root);
        } catch (JsonProcessingException | ResourceAccessException e) {
            assert response != null;
            log.error("fail region id: {}\nresponse body: {}", regionId, response.getBody());
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
                    .fcstDate(item.get("fcstDate").asText())
                    .build();
            results.add(dto);
        }

        return results;
    }


    public List<ForecastDto> fetchWeatherForRegion(ForecastBase base, int nx, int ny, Long regionId) {
        List<WeatherForecastDto> res = this.callWeather(base, nx, ny, regionId);
        if (res == null || res.isEmpty()) {
            return Collections.emptyList();
        }
        return this.mergeForecasts(res);
    }

    public List<ForecastDto> mergeForecasts(List<WeatherForecastDto> rawItems) {
        Map<String, ForecastDto> timeMap = new TreeMap<>();

        for (WeatherForecastDto item : rawItems) {
            String dateTimeKey = item.fcstDate() + item.fcstTime();
            ForecastDto forecastDto = timeMap.computeIfAbsent(dateTimeKey, t -> {
                ForecastDto f = new ForecastDto();
                f.setTime(item.fcstTime());
                f.setDate(item.fcstDate());
                return f;
            });
            ForecastMapper.apply(forecastDto, item);
        }

        for (ForecastDto f : timeMap.values()) {
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

    public Map<Long, List<ForecastDto>> fetchAllWeather(Map<Long, SiGunGuRegion> regionMap) {
        log.info("fetch all forecast....");
        this.weatherMap.clear();

        try {
            this.runWithQueue(regionMap);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Weather fetching interrupted", e);
        }

        return this.weatherMap;
    }

    private void runWithThrottle(
            Long id,
            SiGunGuRegion region,
            Map<Long, List<ForecastDto>> weatherMap,
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
            List<ForecastDto> forecastDtos = this.fetchWeatherForRegion(
                    base,
                    region.grid().nx(),
                    region.grid().ny(),
                    region.id()
            );
            weatherMap.put(id, forecastDtos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 권장되는 처리
        } finally {
            limiter.release();
        }
    }


    public void runWithQueue(Map<Long, SiGunGuRegion> regionMap) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(this.CONSUMER_COUNT + 1);

        // 1. Producer: Queue에 모든 regionId 넣기
        executor.submit(() -> {
            for (Long regionId : regionMap.keySet()) {
                try {
                    this.queue.put(regionId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        for (int i = 0; i < this.CONSUMER_COUNT; i++) {
            executor.submit(() -> {
                while (true) {
                    try {
                        Long regionId = this.queue.poll(5, TimeUnit.SECONDS);
                        if (regionId == null) break;
                        runRegionFetch(regionId, regionMap.get(regionId));
                        Thread.sleep(this.REQUEST_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        log.info("All weather fetched: {}", this.weatherMap.size());
    }

    private void runRegionFetch(Long regionId, SiGunGuRegion region) {
        ForecastBase base = getNearestForecastBase(LocalDate.now(), LocalTime.now());
        List<ForecastDto> dtos = this.fetchWeatherForRegion(base, region.grid().nx(), region.grid().ny(), regionId);
        weatherMap.put(regionId, dtos);
    }

}
