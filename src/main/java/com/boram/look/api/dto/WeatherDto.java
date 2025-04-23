package com.boram.look.api.dto;

import com.boram.look.domain.weather.Forecast;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class WeatherDto {
    private List<Forecast> forecasts;
    private AirQualityDto airQuality;
}
