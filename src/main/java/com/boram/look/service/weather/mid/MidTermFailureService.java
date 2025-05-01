package com.boram.look.service.weather.mid;

import com.boram.look.domain.weather.mid.MidTermFailure;
import com.boram.look.domain.weather.mid.repository.MidTermFailureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidTermFailureService {

    private final MidTermFailureRepository failureRepository;

    @Transactional(readOnly = true)
    public List<MidTermFailure> getAllFailures() {
        return failureRepository.findAll();
    }

    @Transactional
    public void saveFailure(Long sidoId) {
        MidTermFailure failure = MidTermFailure.builder()
                .sidoId(sidoId)
                .build();
        failureRepository.save(failure);
    }
}
