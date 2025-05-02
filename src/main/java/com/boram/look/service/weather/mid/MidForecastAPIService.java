package com.boram.look.service.weather.mid;

import com.boram.look.api.dto.weather.MidForecastRequest;
import com.boram.look.api.dto.weather.MidTemperatureRequest;
import com.boram.look.domain.region.GeoUtil;
import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.domain.weather.forecast.ForecastBase;
import com.boram.look.domain.weather.mid.MidTermForecast;
import com.boram.look.domain.weather.mid.MidTermTemperature;
import com.boram.look.global.util.TimeUtil;
import com.boram.look.service.weather.forecast.ForecastService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MidForecastAPIService {
    @Value("${weather.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private final MidForecastService saverService;
    private final MidTermFailureService failureService;
    private final ForecastService forecastService;

    public void fetch3DaysTermsForecastAndTemperature(SidoRegionCache cache) {
        for (int i = 0; i < 4; i++) {
            LocalDate baseDate = LocalDate.now().plusDays(i);
            ForecastBase base = forecastService.getNearestForecastBase(baseDate, LocalTime.now());
            Point centroid = cache.polygon().getCentroid();
            GridXY gridXY = GeoUtil.toGrid(centroid.getY(), centroid.getX());
            List<Forecast> forecasts = forecastService.fetchWeatherForRegion(base, gridXY.nx(), gridXY.ny(), cache.id());
            float minTemp = forecasts.stream()
                    .map(Forecast::getTemperature)
                    .min(Float::compare)
                    .orElse(Float.NaN);  // 값이 없을 경우 NaN 반환

            float maxTemp = forecasts.stream()
                    .map(Forecast::getTemperature)
                    .max(Float::compare)
                    .orElse(Float.NaN);

            MidTermTemperature temperature = MidTermTemperature.builder()
                    .regId(cache.midTemperatureKey())
                    .forecastDate(baseDate)
                    .maxTemperature(Math.round(maxTemp))
                    .minTemperature(Math.round(minTemp))
                    .build();
            saverService.saveMidTemperature(temperature);

            // 2. 강수확률 (평균 또는 최대 선택)
            double avgPop = forecasts.stream()
                    .mapToInt(Forecast::getPop)
                    .average().orElse(0);

            // 3. 강수형태
            boolean hasRain = forecasts.stream()
                    .anyMatch(f -> f.getPty() > 1);

            MidTermForecast forecast = MidTermForecast.builder()
                    .regId(cache.midTemperatureKey())
                    .forecastDate(baseDate)
                    .rainProbability((int) Math.round(avgPop))
                    .weather(hasRain ? "비" : "맑음")
                    .build();
            saverService.saveMidForecast(forecast);
        }

    }

    public void fetchMidTemperature(SidoRegionCache sidoCache) {
        String midTemperatureURI = this.buildMidTemperatureUrl(sidoCache.midTemperatureKey());
        JsonNode root = this.getMidTemperatureJsonNode(midTemperatureURI, sidoCache.id());
        if (root == null) {
            return;
        }

        MidTemperatureRequest request = parseTemperatureRequest(root);
        if(request == null) { return; }
        saverService.saveMidTermTemperature(request);
    }

    public void fetchMidForecast(SidoRegionCache sidoCache) {
        String midFcstURI = this.buildMidForecastUrl(sidoCache.midFcstKey());
        JsonNode root = this.getMidForecastJsonNode(midFcstURI, sidoCache.id());
        if (root == null) {
            return;
        }

        MidForecastRequest request = parseForecastRequest(root);
        if(request == null) { return; }
        saverService.saveMidTermForecast(request);
    }


    private MidForecastRequest parseForecastRequest(JsonNode root) {
        JsonNode item = root.path("response").path("body").path("items").path("item").get(0);
        if (item == null) {
            log.error("error is occurred in parseForecastRequest.\n jsonNode: {} ",root.asText());
            return null;
        }

        MidForecastRequest request = new MidForecastRequest();
        request.setRegId(item.path("regId").asText());

        for (int day = 4; day <= 10; day++) {
            request.setFieldValue("rnSt" + day, item.path("rnSt" + day).asInt(0));
            request.setFieldValue("wf" + day, item.path("wf" + day).asText(null));
        }

        return request;
    }

    private MidTemperatureRequest parseTemperatureRequest(JsonNode root) {
        JsonNode item = root.path("response").path("body").path("items").path("item").get(0);
        if (item == null) {
            log.error("error is occurred in parseTemperatureRequest.\n jsonNode: {} ",root.asText());
            return null;
        }

        MidTemperatureRequest request = new MidTemperatureRequest();
        request.setRegId(item.path("regId").asText());

        for (int day = 4; day <= 10; day++) {
            request.setFieldValue("taMin" + day, item.path("taMin" + day).asInt(0));
            request.setFieldValue("taMax" + day, item.path("taMax" + day).asInt(0));
        }

        return request;
    }


    private JsonNode getMidTemperatureJsonNode(String uri, Long regionId) {
        ResponseEntity<String> response = restTemplate.getForEntity(URI.create(uri), String.class);
        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.error("mid temperature fail region id: {}\nbody: {}", regionId, response.getBody());
//            failureService.saveFailure(regionId);
            return null;
        }
    }

    private JsonNode getMidForecastJsonNode(String uri, Long regionId) {
        ResponseEntity<String> response = restTemplate.getForEntity(URI.create(uri), String.class);
        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.error("mid forecast fail region id: {}\nbody: {}", regionId, response.getBody());
            failureService.saveFailure(regionId);
            return null;
        }
    }


    private String buildMidForecastUrl(String midKey) {
        String midLandFcst = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
        return UriComponentsBuilder.fromUriString(midLandFcst)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", midKey)
                .queryParam("tmFc", TimeUtil.getToday6AmTime())
                .build(false)
                .toUriString();
    }

    public String buildMidTemperatureUrl(String midKey) {
        String midLandTemp = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
        return UriComponentsBuilder.fromUriString(midLandTemp)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", midKey)
                .queryParam("tmFc", TimeUtil.getToday6AmTime())
                .build(false)
                .toUriString();
    }

}
