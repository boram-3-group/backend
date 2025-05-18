package com.boram.look.domain.weather.forecast.entity;

import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.domain.region.entity.Region;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "forecast")
public class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private String time;
    private float temperature;
    private int sky;
    private int pty;
    private int pop;
    private ForecastIcon icon;
    private Integer iconNumber;
    private String iconMessage;
    @ManyToOne
    private Region region;

    public ForecastDto toDto() {
        return ForecastDto.builder()
                .date(this.date)
                .time(this.time)
                .pop(this.pop)
                .sky(this.sky)
                .pty(this.pty)
                .temperature(this.temperature)
                .icon(this.icon)
                .iconMessage(this.iconMessage)
                .iconNumber(this.iconNumber)
                .build();
    }
}
