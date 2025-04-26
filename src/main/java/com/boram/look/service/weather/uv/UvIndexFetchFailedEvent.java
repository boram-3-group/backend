package com.boram.look.service.weather.uv;

import org.springframework.context.ApplicationEvent;

public class UvIndexFetchFailedEvent extends ApplicationEvent {
    public UvIndexFetchFailedEvent(Object source) {
        super(source);
    }
}
