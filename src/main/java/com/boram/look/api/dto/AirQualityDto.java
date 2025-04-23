package com.boram.look.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AirQualityDto {
    private Integer airQuality;
    private String message;
    private String iconUrl;
}
