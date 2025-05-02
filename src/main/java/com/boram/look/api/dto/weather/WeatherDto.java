package com.boram.look.api.dto.weather;

import com.boram.look.domain.weather.air.AirQualityGrade;
import com.boram.look.domain.weather.forecast.Forecast;
import com.boram.look.domain.weather.forecast.entity.ForecastIcon;
import com.boram.look.domain.weather.uv.UvGrade;
import com.boram.look.domain.weather.uv.UvIndexRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class WeatherDto {
    private List<Forecast> forecasts;
    private AirQualityDto airQuality;
    private UvIndexDto uvIndex;
    private String weatherMessage;

    public void buildWeatherMessage() {
        Forecast currentForecast = forecasts.getFirst();
        if (isRainOrSnow()) {
            this.weatherMessage = currentForecast.getIconMessage();
        } else if (Objects.equals(airQuality.getGrade(), AirQualityGrade.VERY_BAD)) {
            this.weatherMessage = airQuality.getMessage();
        } else if (Objects.equals(uvIndex.getGrade(), UvGrade.EXTREME)) {
            this.weatherMessage = uvIndex.getMessage();
        } else if (Objects.equals(airQuality.getGrade(), AirQualityGrade.BAD)) {
            this.weatherMessage = airQuality.getMessage();
        } else if (Objects.equals(uvIndex.getGrade(), UvGrade.VERY_HIGH)) {
            this.weatherMessage = uvIndex.getMessage();
        } else if (Objects.equals(airQuality.getGrade(), AirQualityGrade.MODERATE)) {
            this.weatherMessage = airQuality.getMessage();
        } else if (Objects.equals(uvIndex.getGrade(), UvGrade.HIGH)) {
            this.weatherMessage = uvIndex.getMessage();
        } else if (Objects.equals(uvIndex.getGrade(), UvGrade.MODERATE)) {
            this.weatherMessage = uvIndex.getMessage();
        } else if (currentForecast.getPty() == 0 && currentForecast.getSky() > 2) {
            this.weatherMessage = "오늘은 흐린 날이에요";
        } else {
            this.weatherMessage = "오늘은 맑고 화창해요";
        }

    }

    private boolean isRainOrSnow() {
        return Objects.equals(forecasts.getFirst().getIcon(), ForecastIcon.RAIN) ||
                Objects.equals(forecasts.getFirst().getIcon(), ForecastIcon.SNOW) ||
                Objects.equals(forecasts.getFirst().getIcon(), ForecastIcon.RAIN_AND_SNOW);
    }

}
