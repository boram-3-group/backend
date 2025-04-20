package com.boram.look.domain.weather;

import com.boram.look.api.dto.WeatherForecastDto;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.boram.look.common.constants.WeatherConstants.KMA_TEMP_3H;
import static com.boram.look.common.constants.WeatherConstants.KMA_PRECIPITATION_PROBABILITY;
import static com.boram.look.common.constants.WeatherConstants.KMA_PRECIPITATION_TYPE;
import static com.boram.look.common.constants.WeatherConstants.KMA_SKY_CONDITION;

public class ForecastMapper {
    private static final Map<String, BiConsumer<Forecast, String>> CATEGORY_APPLIERS = Map.of(
            KMA_TEMP_3H, (f, v) -> f.setTemperature(Float.parseFloat(v)),
            KMA_PRECIPITATION_PROBABILITY, (f, v) -> f.setSky(Integer.parseInt(v)),
            KMA_PRECIPITATION_TYPE, (f, v) -> f.setPty(Integer.parseInt(v)),
            KMA_SKY_CONDITION, (f, v) -> f.setPop(Integer.parseInt(v))
    );

    public static void apply(Forecast forecast, WeatherForecastDto item) {
        BiConsumer<Forecast, String> applier = CATEGORY_APPLIERS.get(item.category());
        if (applier != null) {
            applier.accept(forecast, item.fcstValue());
        }
    }

    public static String getWeatherIcon(int pty, int sky) {
        if (pty == 1 || pty == 4) return "🌧"; // 비 or 소나기
        if (pty == 2 || pty == 3) return "🌨"; // 비/눈, 눈
        if (sky == 1) return "☀️"; // 맑음
        if (sky == 3) return "⛅"; // 구름 많음
        return "☁️"; // 흐림
    }

    public static String getMessage(int pty, int sky) {
        if (pty == 1 || pty == 4) return "비가 오고 있어요";
        if (pty == 2 || pty == 3) return "눈이 내리고 있어요";
        if (sky == 1) return "맑은 날씨입니다";
        if (sky == 3) return "구름이 많아요";
        return "흐린 날씨입니다";
    }

}
