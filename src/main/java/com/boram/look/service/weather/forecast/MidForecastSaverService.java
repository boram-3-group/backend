package com.boram.look.service.weather.forecast;

import com.boram.look.api.dto.weather.MidForecastRequest;
import com.boram.look.domain.region.cache.SidoRegionCache;
import com.boram.look.domain.weather.forecast.entity.MidTermForecast;
import com.boram.look.domain.weather.forecast.entity.MidTermTemperature;
import com.boram.look.domain.weather.forecast.repository.MidTermForecastRepository;
import com.boram.look.domain.weather.forecast.repository.MidTermTemperatureRepository;
import com.boram.look.global.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class MidForecastSaverService {

    private final MidTermForecastRepository forecastRepository;
    private final MidTermTemperatureRepository temperatureRepository;

    @Transactional
    public void saveMidTermTemperature(MidForecastRequest request) {
        LocalDate today = LocalDate.now();

        // 온도 저장 (4~10일)
        for (int day = 4; day <= 10; day++) {
            saveTemperature(today, day, request);
        }
    }

    @Transactional
    public void saveMidTermForecast(MidForecastRequest request) {
        LocalDate today = LocalDate.now();

        // 온도 저장 (4~10일)
        for (int day = 4; day <= 10; day++) {
            saveForecast(today, day, request);
        }
    }


    private void saveForecast(LocalDate today, int day, MidForecastRequest request) {
        LocalDate forecastDate = today.plusDays(day);
        forecastRepository.save(MidTermForecast.builder()
                .regId(request.getRegId())
                .forecastDate(forecastDate)
                .rainProbability(request.getRainProb(day))
                .weather(request.getWeather(day))
                .build());
    }

    private void saveTemperature(LocalDate today, int day, MidForecastRequest request) {
        LocalDate forecastDate = today.plusDays(day);
        temperatureRepository.save(MidTermTemperature.builder()
                .regId(request.getRegId())
                .forecastDate(forecastDate)
                .minTemperature(request.getMinTemperature(day))
                .maxTemperature(request.getMaxTemperature(day))
                .build());
    }

}
