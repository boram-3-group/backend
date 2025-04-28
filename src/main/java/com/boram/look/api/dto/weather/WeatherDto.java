package com.boram.look.api.dto.weather;

import com.boram.look.domain.weather.forecast.Forecast;
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
    private UvIndexDto uvIndex;
}
