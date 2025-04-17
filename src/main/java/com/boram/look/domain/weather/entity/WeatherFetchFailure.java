package com.boram.look.domain.weather.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_fetch_failures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WeatherFetchFailure {

    @Id
    private Long regionId; // 시군구 ID 기준

    private LocalDateTime lastFailedAt;

    private int failCount;

    public void updateFailureTime() {
        this.lastFailedAt = LocalDateTime.now();
        this.failCount++;
    }
}
