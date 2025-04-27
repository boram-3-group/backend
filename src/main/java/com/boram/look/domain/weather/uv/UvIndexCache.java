package com.boram.look.domain.weather.uv;

import lombok.Builder;


@Builder
public record UvIndexCache(
        String sido,
        Integer h0,
        Integer h3,
        Integer h6,
        String fetchedTime
) {
}
