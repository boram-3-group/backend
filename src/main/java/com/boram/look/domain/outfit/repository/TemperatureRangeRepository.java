package com.boram.look.domain.outfit.repository;

import com.boram.look.domain.outfit.TemperatureRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemperatureRangeRepository extends JpaRepository<TemperatureRange, Integer> {
    List<TemperatureRange> findByIdIn(List<Integer> idList);

    @Query("SELECT f FROM TemperatureRange f WHERE :temp >= f.min AND :temp < f.max")
    Optional<TemperatureRange> findByTemperature(@Param("temp") float temperature);

}