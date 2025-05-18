package com.boram.look.service.outfit;

import com.boram.look.api.dto.outfit.OutfitDto;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.user.constants.Gender;
import com.boram.look.api.dto.weather.ForecastDto;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.boram.look.service.region.RegionCacheService;
import com.boram.look.service.weather.forecast.ForecastCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutfitFacade {
    private final OutfitService outfitService;
    private final RegionCacheService regionCacheService;
    private final ForecastCacheService forecastCacheService;

    public OutfitDto.Transfer getOutfitByPosition(
            float longitude,
            float latitude,
            Integer eventTypeId,
            Gender gender,
            PrincipalDetails principal
    ) {
        UUID userId = principal != null ? principal.getUser().getId() : null;
        SiGunGuRegion region = regionCacheService.findRegionByLocation(latitude, longitude).orElseThrow(ResourceNotFoundException::new);
        List<ForecastDto> forecastDtos = forecastCacheService.getForecast(region.id());
        return outfitService.matchOutfit(eventTypeId, forecastDtos, gender, userId);
    }

    public OutfitDto.Transfer getOutfitByPosition(
            float longitude,
            float latitude,
            Integer eventTypeId,
            Gender gender
    ) {
        SiGunGuRegion region = regionCacheService.findRegionByLocation(latitude, longitude).orElseThrow(ResourceNotFoundException::new);
        List<ForecastDto> forecastDtos = forecastCacheService.getForecast(region.id());
        return outfitService.matchOutfit(eventTypeId, forecastDtos, gender, null);
    }

}
