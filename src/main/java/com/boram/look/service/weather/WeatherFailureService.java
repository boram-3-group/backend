package com.boram.look.service.weather;

import com.boram.look.domain.weather.entity.WeatherFetchFailure;
import com.boram.look.domain.weather.repository.WeatherFetchFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherFailureService {

    private final WeatherFetchFailureRepository repository;

    public void saveFailure(Long regionId) {
        repository.findById(regionId)
                .ifPresentOrElse(
                        fail -> {
                            fail.updateFailureTime();
                            repository.save(fail);
                        },
                        () -> repository.save(WeatherFetchFailure.builder()
                                .regionId(regionId)
                                .lastFailedAt(java.time.LocalDateTime.now())
                                .failCount(1)
                                .build())
                );
    }

    public void removeFailure(Long regionId) {
        repository.deleteById(regionId);
    }

    public List<WeatherFetchFailure> findAllFailures() {
        return repository.findAll();
    }
}