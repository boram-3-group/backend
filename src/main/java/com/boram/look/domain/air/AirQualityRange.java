package com.boram.look.domain.air;

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
}
