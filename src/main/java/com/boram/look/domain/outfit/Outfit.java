package com.boram.look.domain.outfit;

import com.boram.look.domain.s3.FileMetadata;
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

    @ManyToOne
    @JoinColumn(name = "temperature_range_id")
    private TemperatureRange temperatureRange;

    @OneToMany
    private List<FileMetadata> fileMetadata;

    @OneToMany(mappedBy = "outfit", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OutfitImage> images = new ArrayList<>();
}
