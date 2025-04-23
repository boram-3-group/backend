package com.boram.look.domain.forecast.repository;

import com.boram.look.domain.forecast.entity.ForecastFetchFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForecastFetchFailureRepository extends JpaRepository<ForecastFetchFailure, Long> {
}
