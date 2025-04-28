package com.boram.look.api.dto.weather;

import com.boram.look.global.constant.WeatherConstants;
import lombok.Builder;

/**
 * @param fcstTime  예보 시간
 * @param category  항목 이름 {@link WeatherConstants}
 * @param fcstValue 항목에 대한 값
 * @author thekim123
 */
@Builder
public record WeatherForecastDto(
        String fcstTime,
        String category,
        String fcstValue
) {
}
