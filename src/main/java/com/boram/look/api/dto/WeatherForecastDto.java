package com.boram.look.api.dto;

import lombok.Builder;

/**
 * @param fcstTime  예보 시간
 * @param category  항목 이름 {@link com.boram.look.common.constants.WeatherConstants}
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
