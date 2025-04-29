package com.boram.look.service.weather.forecast;

import com.boram.look.api.dto.weather.MidForecastRequest;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.global.util.TimeUtil;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class MidForecastAPIService {
    @Value("${weather.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private final MidForecastSaverService saverService;

    public void getMidTemperature(SidoRegionCache sidoCache) {
        URI midTemperatureURI = this.buildMidTemperatureUrl(sidoCache.midTemperatureKey());
        JsonNode root = this.getJsonNode(midTemperatureURI, sidoCache.id());
        if (root == null) {
            return;
        }

        MidForecastRequest request = parseTemperatureRequest(root);
        saverService.saveMidTermTemperature(request);
    }

    public void getMidForecast(SidoRegionCache sidoCache) {
        URI midFcstURI = this.buildMidForecastUrl(sidoCache.midFcstKey());
        JsonNode root = this.getJsonNode(midFcstURI, sidoCache.id());
        if (root == null) {
            return;
        }

        MidForecastRequest request = parseForecastRequest(root);
        saverService.saveMidTermForecast(request);
    }


    public MidForecastRequest parseForecastRequest(JsonNode root) {
        JsonNode item = root.path("response").path("body").path("items").path("item").get(0);

        MidForecastRequest request = new MidForecastRequest();
        request.setRegId(item.path("regId").asText());

        for (int day = 4; day <= 10; day++) {
            request.setFieldValue("rnSt" + day, item.path("rnSt" + day).asInt(0));
            request.setFieldValue("wf" + day, item.path("wf" + day).asText(null));
        }

        return request;
    }

    public MidForecastRequest parseTemperatureRequest(JsonNode root) {
        JsonNode item = root.path("response").path("body").path("items").path("item").get(0);

        MidForecastRequest request = new MidForecastRequest();
        request.setRegId(item.path("regId").asText());

        for (int day = 4; day <= 10; day++) {
            request.setFieldValue("taMin" + day, item.path("taMin" + day).asInt(0));
            request.setFieldValue("taMax" + day, item.path("taMax" + day).asInt(0));
        }

        return request;
    }


    private JsonNode getJsonNode(URI uri, Long regionId) {
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.error("fail region id: {}\nbody: {}", regionId, response.getBody());
//            failureService.saveFailure(regionId);
            return null;
        }
    }


    private URI buildMidForecastUrl(String midKey) {
        String midLandFcst = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
        return UriComponentsBuilder.fromUriString(midLandFcst)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", midKey)
                .queryParam("tmFc", TimeUtil.getToday6AmTime())
                .build(false)
                .toUri();
    }

    public URI buildMidTemperatureUrl(String midKey) {
        String midLandTemp = "https://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
        return UriComponentsBuilder.fromUriString(midLandTemp)
                .queryParam("serviceKey", this.serviceKey)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", midKey)
                .queryParam("tmFc", TimeUtil.getToday6AmTime())
                .build(false)
                .toUri();
    }
}
