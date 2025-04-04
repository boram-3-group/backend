package com.boram.look.service.user;

import com.boram.look.api.dto.UserDto;
import com.boram.look.domain.user.entity.StyleType;
import com.boram.look.domain.user.entity.ThermoSensitivity;
import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.repository.StyleTypeRepository;
import com.boram.look.domain.user.repository.ThermoSensitivityRepository;
import com.boram.look.domain.user.repository.UserRepository;
import com.boram.look.global.ex.ResourceNotFoundException;
import com.boram.look.service.user.helper.StyleTypesHelper;
import com.boram.look.service.user.helper.ThermoSensitivityHelper;
import com.boram.look.service.user.helper.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ThermoSensitivityRepository thermoRepository;
    private final StyleTypeRepository styleRepository;

    @Transactional
    public void joinUser(UserDto.Save dto) {
        ThermoSensitivity sensitivity = ThermoSensitivityHelper.findThermo(dto.getThermoId(), thermoRepository);
        Set<StyleType> styleTypes = StyleTypesHelper.findStyleTypes(dto.getStyleTypeIds(), styleRepository);
        User user = dto.toEntity(sensitivity, styleTypes);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, UserDto.Save dto) {
        ThermoSensitivity sensitivity = ThermoSensitivityHelper.findThermo(dto.getThermoId(), thermoRepository);
        Set<StyleType> styleTypes = StyleTypesHelper.findStyleTypes(dto.getStyleTypeIds(), styleRepository);
        User user = UserServiceHelper.findUser(userId, userRepository);
        user.update(dto, sensitivity, styleTypes);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User deleteUser = UserServiceHelper.findUser(userId, userRepository);
        userRepository.delete(deleteUser);
    }

    @Transactional(readOnly = true)
    public UserDto.Profile getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);
        return user.toDto();
    }
}
