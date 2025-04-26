package com.boram.look.domain.weather.uv;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UvIndexCache(
        String sido,
        Integer h0,
        Integer h3,
        Integer h6,
        LocalDateTime fetchedTime
) {
}
