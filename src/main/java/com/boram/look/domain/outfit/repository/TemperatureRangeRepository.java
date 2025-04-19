package com.boram.look.domain.outfit.repository;

import com.boram.look.domain.outfit.TemperatureRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemperatureRangeRepository extends JpaRepository<TemperatureRange, Integer> {
    List<TemperatureRange> findByIdIn(List<Integer> idList);
}