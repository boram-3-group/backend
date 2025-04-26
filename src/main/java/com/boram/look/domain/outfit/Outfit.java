package com.boram.look.domain.outfit;

import com.boram.look.api.dto.FileDto;
import com.boram.look.api.dto.OutfitDto;
import com.boram.look.domain.condition.EventType;
import com.boram.look.domain.condition.TemperatureRange;
import com.boram.look.domain.user.constants.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "outfit")
public class Outfit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "temperature_range_id")
    private TemperatureRange temperatureRange;

    @OneToMany(mappedBy = "outfit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OutfitImage> images = new ArrayList<>();

    public void update(
            EventType eventType,
            TemperatureRange temperatureRange,
            Gender gender
    ) {
        this.eventType = eventType;
        this.temperatureRange = temperatureRange;
        this.gender = gender;
    }

    public OutfitDto.Transfer toDto(List<FileDto> images) {
        return OutfitDto.Transfer.builder()
                .id(this.id)
                .eventType(this.eventType.getCategoryName())
                .temperatureRange(this.temperatureRange.getMin() + "~" + this.temperatureRange.getMax())
                .fileMetadata(images)
                .build();
    }
}
