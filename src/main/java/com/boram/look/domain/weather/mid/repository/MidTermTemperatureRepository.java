package com.boram.look.domain.weather.mid.repository;

import com.boram.look.domain.weather.mid.MidTermTemperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface MidTermTemperatureRepository extends JpaRepository<MidTermTemperature, Long> {

    Optional<MidTermTemperature> findByForecastDateAndRegId(LocalDate forecastDate, String regId);

    @Modifying
    @Query("""
            DELETE MidTermTemperature  t
            WHERE t.forecastDate < :today
            """)
    void findTemperatureBeforeDate(LocalDate today);
}