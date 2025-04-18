package com.boram.look.service.weather;

import com.boram.look.api.dto.WeatherForecastDto;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.domain.weather.ForecastBase;
import com.boram.look.domain.weather.WeatherMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final WeatherFailureService failureService;

    @Value("${weather.vilage-fcst-url}")
    private String vilageFcstUrl;
    @Value("${weather.service-key}")
    private String serviceKey;


    public List<WeatherForecastDto> callWeather(int nx, int ny, Long regionId) {
        ForecastBase base = getNearestForecastBase();
        String url = this.buildWeatherRequestUrl(base, nx, ny);
        URI uri = URI.create(url); // 문자열을 URI 객체로 변환
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.error("fail region id: {}\nbody: {}", regionId, response.getBody());
            failureService.saveFailure(regionId);
            return new ArrayList<>();
        }
        JsonNode items = root.path("response").path("body").path("items").path("item");

        // 간단히 매핑
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

            switch (item.category()) {
                case "T3H" -> forecast.setTemperature(Integer.parseInt(item.fcstValue()));
                case "SKY" -> forecast.setSky(Integer.parseInt(item.fcstValue()));
                case "PTY" -> forecast.setPty(Integer.parseInt(item.fcstValue()));
                case "POP" -> forecast.setPop(Integer.parseInt(item.fcstValue()));
            }
        }

        for (Forecast f : timeMap.values()) {
            f.setIcon(WeatherMapper.getWeatherIcon(f.getPty(), f.getSky()));
            f.setMessage(WeatherMapper.getMessage(f.getPty(), f.getSky()));
        }

        return new ArrayList<>(timeMap.values());
    }

    public ForecastBase getNearestForecastBase() {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        List<String> baseTimes = List.of("0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300");

        for (int i = baseTimes.size() - 1; i >= 0; i--) {
            String time = baseTimes.get(i);
            LocalTime baseTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));

            if (now.isAfter(baseTime) || now.equals(baseTime)) {
                return new ForecastBase(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")), time);
            }
        }

        // 자정 직후 → 전날 23시 예보
        return new ForecastBase(today.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "2300");
    }

    public Map<Long, List<Forecast>> fetchAllWeather(Map<Long, SiGunGuRegion> regionMap) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Semaphore limiter = new Semaphore(30);
        List<Future<Void>> futures = new ArrayList<>();

        Map<Long, List<Forecast>> weatherMap = new HashMap<>();

        AtomicInteger count = new AtomicInteger();
        for (Map.Entry<Long, SiGunGuRegion> entry : regionMap.entrySet()) {
            Long id = entry.getKey();
            SiGunGuRegion region = entry.getValue();
            futures.add(executor.submit(() -> {
                try {
                    limiter.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    int current = count.incrementAndGet();
                    // 30개 단위로 1초 대기
                    if (current % 30 == 0) {
                        Thread.sleep(1000);
                    }

                    List<Forecast> forecasts = this.fetchWeatherForRegion(
                            region.grid().nx(),
                            region.grid().ny(),
                            region.id()
                    );
                    synchronized (weatherMap) {
                        weatherMap.put(id, forecasts);
                    }
                } finally {
                    limiter.release();
                }

                return null;
            }));


        }

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

}
