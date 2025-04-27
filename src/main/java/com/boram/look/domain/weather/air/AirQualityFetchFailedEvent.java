package com.boram.look.domain.weather.air;

import org.springframework.context.ApplicationEvent;

public class AirQualityFetchFailedEvent extends ApplicationEvent {
    public AirQualityFetchFailedEvent(Object source) {
        super(source);
    }
}
