package com.boram.look.api.dto.outfit;

import com.boram.look.api.dto.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class OutfitImageDto {
    private Long id;
    private String title;
    private String description;
    private final FileDto metadata;
    private boolean bookmarked;
}
