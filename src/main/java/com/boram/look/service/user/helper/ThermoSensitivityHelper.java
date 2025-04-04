package com.boram.look.service.user.helper;

import com.boram.look.domain.user.entity.ThermoSensitivity;
import com.boram.look.domain.user.repository.ThermoSensitivityRepository;
import jakarta.persistence.EntityNotFoundException;

public class ThermoSensitivityHelper {
    public static ThermoSensitivity findThermo(Integer id, ThermoSensitivityRepository repository) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("성질을 찾을 수 없습니다."));
    }
}
