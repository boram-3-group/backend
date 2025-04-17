package com.boram.look.service.weather;

import com.boram.look.api.dto.WeatherForecastDto;
import com.boram.look.api.dto.WeatherResponse;
import com.boram.look.domain.weather.Forecast;
import com.boram.look.domain.weather.WeatherMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${weather.vilage-fcst-url}")
    private String vilageFcstUrl;
    @Value("${weather.service-key}")
    private String serviceKey;


    public List<WeatherForecastDto> callWeather(int nx, int ny)  {
        String url = UriComponentsBuilder.fromHttpUrl(this.vilageFcstUrl)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .queryParam("base_time", getNearestForecastTime()) // 0200, 0500 ...
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(false)
                .toUriString();
        URI uri = URI.create(url); // 문자열을 URI 객체로 변환
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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

    public List<Forecast> fetchWeatherForRegion(int nx, int ny) {
        List<WeatherForecastDto> res = this.callWeather(nx, ny);
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

    public String getNearestForecastTime() {
        LocalTime now = LocalTime.now();
        List<String> baseTimes = List.of("0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300");

        for (int i = baseTimes.size() - 1; i >= 0; i--) {
            String time = baseTimes.get(i);
            LocalTime t = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
            if (now.isAfter(t) || now.equals(t)) {
                return time;
            }
        }
        return "2300"; // 자정 직후면 전날 23:00 예보
    }
}
