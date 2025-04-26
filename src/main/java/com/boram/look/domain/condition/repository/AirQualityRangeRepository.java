package com.boram.look.domain.condition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AirQualityRangeRepository extends JpaRepository<AirQualityRange, Long> {

    @Query("SELECT q FROM AirQualityRange q WHERE :value >= q.min AND :value < q.max")
    Optional<AirQualityRange> getByCurrentQuality(@Param("value") Integer currentValue);

}
