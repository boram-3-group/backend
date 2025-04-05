package com.boram.look.service.user;

import com.boram.look.api.dto.SensitivityDto;
import com.boram.look.domain.Action;
import com.boram.look.domain.user.entity.StyleType;
import com.boram.look.domain.user.entity.ThermoSensitivity;
import com.boram.look.domain.user.repository.ThermoSensitivityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThermoSensitivityService {

    private final ThermoSensitivityRepository repository;

    @Transactional
    public void doEdit(List<SensitivityDto.Edit> dto) {
        Map<Action, List<SensitivityDto.Edit>> grouped =
                dto.stream().collect(Collectors.groupingBy(SensitivityDto.Edit::getAction));

        List<SensitivityDto.Edit> creates = grouped.getOrDefault(Action.CREATE, List.of());
        creates.forEach(command ->
                repository.save(new ThermoSensitivity(command.getContent()))
        );

        List<SensitivityDto.Edit> updates = grouped.getOrDefault(Action.UPDATE, List.of());
        updates.forEach(command -> {
            ThermoSensitivity existing = repository.findById(command.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Not found: " + command.getId()));
            existing.updateContent(command.getContent());
        });

        List<SensitivityDto.Edit> deletes = grouped.getOrDefault(Action.DELETE, List.of());
        deletes.forEach(command -> repository.deleteById(command.getId()));

        log.info("""
                            create request count: {}
                            update request count: {}
                            delete request count: {}
                        """,
                creates.size(),
                updates.size(),
                deletes.size()
        );
    }

    @Transactional(readOnly = true)
    public Page<SensitivityDto.Get> getThermoSensitivities(Pageable pageable) {
        Page<ThermoSensitivity> pages = repository.findAll(pageable);
        return pages.map(ThermoSensitivity::toDto);
    }
}
