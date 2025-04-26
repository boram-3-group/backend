package com.boram.look.domain.weather.air;

import org.springframework.context.ApplicationEvent;

//TODO: 자외선 api 연동 실패시 핸들링
public class AirQualityFetchFailedEvent extends ApplicationEvent {
    public AirQualityFetchFailedEvent(Object source) {
        super(source);
    }
}
