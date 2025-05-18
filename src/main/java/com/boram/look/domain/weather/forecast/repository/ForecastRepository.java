package com.boram.look.domain.weather.forecast.repository;

import com.boram.look.domain.region.entity.Region;
import com.boram.look.domain.weather.forecast.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    void deleteByRegionId(Long regionId);

    List<Forecast> findByRegionAndDateBetween(Region region, String startDate, String endDate);


}
