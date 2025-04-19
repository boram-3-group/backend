package com.boram.look.domain.outfit;

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
    private Long id;
    private float min;
    private float max;
}
