package com.boram.look.api.dto.weather;

import com.boram.look.domain.weather.mid.MidTermForecast;
import com.boram.look.domain.weather.mid.MidTermTemperature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@ToString
@Schema(description = "10일 날씨 dto")
public class MidTermForecastDto {
    @Schema(description = "예보 날짜")
    private final LocalDate forecastDate;
    @Schema(description = "강수확률(%)")
    private final Integer rainProbability;
    @Schema(description = "날씨 (맑음, 흐림 등)")
    private final String weather;
    @Schema(description = "최저기온")
    private final Integer minTemperature;
    @Schema(description = "최고기온")
    private final Integer maxTemperature;

    public MidTermForecastDto(MidTermForecast forecast, MidTermTemperature temperature) {
        this.forecastDate = forecast.getForecastDate();
        this.rainProbability = forecast.getRainProbability();
        this.weather = forecast.getWeather();
        this.minTemperature = temperature.getMinTemperature();
        this.maxTemperature = temperature.getMaxTemperature();
    }


}
