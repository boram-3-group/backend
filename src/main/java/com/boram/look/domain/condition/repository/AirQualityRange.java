package com.boram.look.domain.condition.repository;

import com.boram.look.api.dto.weather.AirQualityDto;
import com.boram.look.domain.weather.air.AirQualityGrade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "air_quality_range")
public class AirQualityRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer min;
    private Integer max;
    @Column(name = "icon_flag")
    private Boolean iconFlag;
    private String message;
    @Enumerated(EnumType.STRING)
    private AirQualityGrade grade;

    public AirQualityDto toDto(Integer currentValue) {
        return AirQualityDto.builder()
                .airQuality(currentValue)
                .message(this.message)
                .iconFlag(this.iconFlag)
                .grade(this.grade)
                .build();
    }
}
