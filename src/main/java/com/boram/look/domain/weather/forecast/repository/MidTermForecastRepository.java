package com.boram.look.domain.weather.forecast.repository;


import com.boram.look.domain.weather.forecast.entity.MidTermForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MidTermForecastRepository extends JpaRepository<MidTermForecast, Long> {
    List<MidTermForecast> findByRegIdAndForecastDateBetweenOrderByForecastDateAsc(
            String regId, LocalDate startDate, LocalDate endDate
    );
}