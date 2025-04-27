package com.boram.look.domain.weather.uv.repository;

import com.boram.look.domain.weather.uv.UvIndexRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UvRangeRepository extends JpaRepository<UvIndexRange, Long> {
    @Query("SELECT u FROM UvIndexRange u WHERE :value >= u.min AND :value < u.max")
    Optional<UvIndexRange> getByCurrentQuality(@Param("value") Integer currentValue);
}
