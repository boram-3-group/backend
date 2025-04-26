package com.boram.look.domain.outfit.repository;

import com.boram.look.domain.condition.EventType;
import com.boram.look.domain.outfit.Outfit;
import com.boram.look.domain.condition.TemperatureRange;
import com.boram.look.domain.user.constants.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {

    Optional<Outfit> findByEventTypeAndTemperatureRangeAndGender(EventType eventType, TemperatureRange temperatureRange, Gender gender);

}
