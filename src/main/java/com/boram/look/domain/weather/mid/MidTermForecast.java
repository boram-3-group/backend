package com.boram.look.domain.weather.mid;


import com.boram.look.domain.AuditingFields;
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
public class MidTermForecast extends AuditingFields {

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

    public void update(LocalDate date, Integer rainProb, String weather) {
        this.forecastDate = date;
        this.rainProbability = rainProb;
        this.weather = weather;
    }

}