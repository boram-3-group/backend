package com.boram.look.domain.condition;

import com.boram.look.api.dto.outfit.TemperatureRangeDto;
import com.boram.look.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "temperature_range")
public class TemperatureRange extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(precision = 5, scale = 2)
    private BigDecimal min;
    @Column(precision = 5, scale = 2)
    private BigDecimal max;

    public void update(BigDecimal min, BigDecimal max) {
        this.max = max;
        this.min = min;
    }

    public TemperatureRangeDto.Copy toDto() {
        return TemperatureRangeDto.Copy.builder()
                .id(this.id)
                .max(this.max)
                .min(this.min)
                .build();
    }
}
