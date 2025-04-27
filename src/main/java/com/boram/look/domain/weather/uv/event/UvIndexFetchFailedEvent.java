package com.boram.look.domain.weather.uv.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UvIndexFetchFailedEvent extends ApplicationEvent {

    private final String sido;

    public UvIndexFetchFailedEvent(Object source, String sido) {
        super(source);
        this.sido = sido;
    }
}
