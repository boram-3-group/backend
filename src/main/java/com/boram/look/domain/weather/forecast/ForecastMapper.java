package com.boram.look.domain.weather.forecast;

import com.boram.look.api.dto.weather.WeatherForecastDto;
import com.boram.look.domain.weather.forecast.entity.ForecastIcon;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.boram.look.global.constant.WeatherConstants.KMA_TEMP_3H;
import static com.boram.look.global.constant.WeatherConstants.KMA_PRECIPITATION_PROBABILITY;
import static com.boram.look.global.constant.WeatherConstants.KMA_PRECIPITATION_TYPE;
import static com.boram.look.global.constant.WeatherConstants.KMA_SKY_CONDITION;

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

    public static ForecastIcon getWeatherIcon(String time, int pty, int sky) {
        boolean isDayTime = isDayTime(time);

        if (pty == 1 || pty == 4) {
            return ForecastIcon.RAIN;
        } else if (pty == 2) {
            return ForecastIcon.RAIN_AND_SNOW;
        } else if (pty == 3) {
            return ForecastIcon.SNOW;
        } else {
            // pty == 0 (비 안 오는 경우)
            if (sky == 1 || sky == 2) {
                return isDayTime ? ForecastIcon.CLEAR_DAY : ForecastIcon.CLEAR_NIGHT;
            } else if (sky == 3 || sky == 4) {
                return isDayTime ? ForecastIcon.MOSTLY_CLOUDY_DAY : ForecastIcon.MOSTLY_CLOUDY_NIGHT;
            } else {
                return ForecastIcon.CLOUDY; // SKY 코드가 예상 밖이면 기본 흐림으로
            }
        }
    }

    private static boolean isDayTime(String time) {
        if (time == null || time.length() != 4) {
            return true; // 안전하게 기본 낮 처리
        }
        int hour = Integer.parseInt(time.substring(0, 2));
        return hour >= 6 && hour < 18;
    }

}
