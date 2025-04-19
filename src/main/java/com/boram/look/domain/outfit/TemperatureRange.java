package com.boram.look.domain.outfit;

import com.boram.look.api.dto.TemperatureRangeDto;
import com.boram.look.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

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
    private float min;
    private float max;

    public void update(float min, float max) {
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
