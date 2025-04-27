package com.boram.look.domain.weather.uv.repository;

import com.boram.look.domain.weather.uv.entity.UvFetchFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UvFetchFailureRepository extends JpaRepository<UvFetchFailure, Long> {
}
