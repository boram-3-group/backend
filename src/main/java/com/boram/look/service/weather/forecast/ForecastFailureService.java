package com.boram.look.service.weather.forecast;

import com.boram.look.domain.forecast.entity.ForecastFetchFailure;
import com.boram.look.domain.forecast.repository.ForecastFetchFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastFailureService {

    private final ForecastFetchFailureRepository repository;

    @Transactional(readOnly = true)
    public void saveFailure(Long regionId) {
        repository.findById(regionId)
                .ifPresentOrElse(
                        fail -> {
                            fail.updateFailureTime();
                            repository.save(fail);
                        },
                        () -> repository.save(ForecastFetchFailure.builder()
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
        repository.findById(regionId).ifPresent(ForecastFetchFailure::updateFailureTime);
    }

    @Transactional(readOnly = true)
    public List<ForecastFetchFailure> findAllFailures() {
        return repository.findAll();
    }
}