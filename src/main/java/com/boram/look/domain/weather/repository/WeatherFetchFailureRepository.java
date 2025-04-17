package com.boram.look.domain.weather.repository;

import com.boram.look.domain.weather.entity.WeatherFetchFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherFetchFailureRepository extends JpaRepository<WeatherFetchFailure, Long> {
}
