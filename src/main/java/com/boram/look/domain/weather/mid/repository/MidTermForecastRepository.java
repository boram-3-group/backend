package com.boram.look.domain.weather.mid.repository;


import com.boram.look.api.dto.weather.MidTermForecastDto;
import com.boram.look.domain.weather.mid.MidTermForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MidTermForecastRepository extends JpaRepository<MidTermForecast, Long> {
    List<MidTermForecast> findByRegIdAndForecastDateBetweenOrderByForecastDateAsc(
            String regId, LocalDate startDate, LocalDate endDate
    );

    Optional<MidTermForecast> findByForecastDateAndRegId(LocalDate today, String regId);

    @Query("""
            SELECT new com.boram.look.api.dto.weather.MidTermForecastDto(f, t)
            FROM MidTermForecast f
            JOIN MidTermTemperature t ON f.regId = t.regId AND f.forecastDate = t.forecastDate
            WHERE f.regId = :regId AND f.forecastDate BETWEEN :start AND :end
            """)
    List<MidTermForecastDto> find10DaysMidTermForecasts(
            @Param("regId") String tempKey,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Modifying
    @Query("""
            DELETE MidTermForecast f
            where f.forecastDate < :today
            """)
    void deleteForecastBeforeDate(LocalDate today);
}