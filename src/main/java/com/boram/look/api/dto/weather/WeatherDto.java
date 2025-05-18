package com.boram.look.api.dto.weather;

import com.boram.look.domain.weather.air.AirQualityGrade;
import com.boram.look.domain.weather.forecast.entity.ForecastIcon;
import com.boram.look.domain.weather.uv.UvGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@AllArgsConstructor
@Builder
public class WeatherDto {
    private List<ForecastDto> forecasts;
    private AirQualityDto airQuality;
    private UvIndexDto uvIndex;
    private String weatherMessage;


    public void buildWeatherMessage() {
        Optional<ForecastDto> firstRain = forecasts.stream()
                .filter(forecast -> forecast.getPty() > 0)
                .findFirst();
        if (firstRain.isPresent()) {
            int hour = Integer.parseInt(firstRain.get().getTime()) / 100;
            String period = hour < 12 ? "오전" : "오후";
            int hour12 = hour <= 12 ? hour : hour - 12;
            if (Objects.equals(firstRain.get().getIcon(), ForecastIcon.SNOW)) {
                this.weatherMessage = "오늘 " + period + " " + hour12 + "시부터 눈 예보, 미끄럼 주의!";
            } else {
                this.weatherMessage = "오늘 " + period + " " + hour12 + "시부터 비 예보, 우산 필요!";
            }
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
        } else if (forecasts.getFirst().getSky() > 2) {
            this.weatherMessage = "오늘은 흐린 날이에요";
        } else {
            this.weatherMessage = "오늘은 맑고 화창해요";
        }

    }

}
