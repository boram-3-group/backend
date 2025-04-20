package com.boram.look.domain.outfit;

import com.boram.look.api.dto.FileDto;
import com.boram.look.domain.s3.FileMetadata;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "outfit_image")
public class OutfitImage {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Outfit outfit;

    @ManyToOne
    private FileMetadata fileMetadata;
}