package com.boram.look.domain.weather.forecast.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mid_term_forecast")
public class MidTermForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String regId; // 지역 ID

    @Column(nullable = false)
    private LocalDate forecastDate; // 예보 날짜 (오늘 + n일)

    @Column
    private Integer rainProbability; // 강수확률(%)

    @Column(length = 20)
    private String weather; // 날씨 (맑음, 흐림 등)

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }

}