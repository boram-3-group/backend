package com.boram.look.service.outfit;

import com.boram.look.api.dto.OutfitDto;
import com.boram.look.domain.outfit.EventType;
import com.boram.look.domain.outfit.Outfit;
import com.boram.look.domain.outfit.TemperatureRange;
import com.boram.look.domain.outfit.repository.EventTypeRepository;
import com.boram.look.domain.outfit.repository.TemperatureRangeRepository;
import com.boram.look.domain.s3.FileMetadata;
import com.boram.look.global.ex.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutfitFacade {

    private final TemperatureRangeRepository temperatureRangeRepository;
    private final EventTypeRepository eventTypeRepository;

    public void registerOutfits(OutfitDto.Insert dto) {

    }



}
