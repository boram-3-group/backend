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
@Table(name = "mid_term_temperature")
public class MidTermTemperature extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String regId; // 지역 ID

    @Column(nullable = false)
    private LocalDate forecastDate; // 예보 날짜 (오늘 + n일)

    @Column(name = "min_temperature")
    private Integer minTemperature; // 최저기온

    @Column(name = "max_temperature")
    private Integer maxTemperature; // 최고기온

    public void update(LocalDate date, Integer min, Integer max) {
        this.forecastDate = date;
        this.minTemperature = min;
        this.maxTemperature = max;
    }

}
