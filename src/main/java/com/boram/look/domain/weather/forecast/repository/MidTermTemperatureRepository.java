package com.boram.look.domain.weather.forecast.repository;

import com.boram.look.domain.weather.forecast.entity.MidTermTemperature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MidTermTemperatureRepository extends JpaRepository<MidTermTemperature, Long> {
    List<MidTermTemperature> findByRegIdAndForecastDateBetweenOrderByForecastDateAsc(
            String regId, LocalDate startDate, LocalDate endDate
    );
}