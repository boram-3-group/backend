package com.boram.look.service.weather;

import com.boram.look.domain.weather.entity.WeatherFetchFailure;
import com.boram.look.domain.weather.repository.WeatherFetchFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherFailureService {

    private final WeatherFetchFailureRepository repository;

    @Transactional(readOnly = true)
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

    @Transactional
    public void removeFailure(Long regionId) {
        repository.deleteById(regionId);
    }

    /**
     * DB에서 조회해서 있으면 실패에 대한 데이터 갱신하고,
     * 없으면 내버려둠
     *
     * @param regionId 지역 ID
     */
    @Transactional
    public void updateFailureTime(Long regionId) {
        repository.findById(regionId).ifPresent(WeatherFetchFailure::updateFailureTime);
    }

    @Transactional(readOnly = true)
    public List<WeatherFetchFailure> findAllFailures() {
        return repository.findAll();
    }
}