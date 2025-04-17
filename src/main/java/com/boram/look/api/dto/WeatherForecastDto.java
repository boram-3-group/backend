package com.boram.look.api.dto;

import lombok.Builder;

@Builder
public record WeatherForecastDto(
        String fcstTime,      // "0900"
        String category,      // "SKY", "PTY", ...
        String fcstValue     // 값 (예: "1", "맑음", "15")
) {
}
