package com.boram.look.api.dto;

import com.boram.look.domain.user.constants.Gender;
import lombok.Builder;

import java.util.List;

public class OutfitDto {

    @Builder
    public record Insert(
            Integer eventTypeId,
            Integer temperatureRangeId,
            Gender gender
    ) {
    }

    public record Image(
            String description
    ) {
    }

    @Builder
    public record Transfer(
            Long id,
            String eventType,
            String temperatureRange,
            List<FileDto> fileMetadata
    ) {
    }

}
