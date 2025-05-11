package com.boram.look.service.notification;

import com.boram.look.api.dto.notification.UserNotificationSettingDto;
import com.boram.look.domain.notification.UserNotificationSetting;
import com.boram.look.domain.notification.repository.UserNotificationSettingRepository;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.security.authentication.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationSettingService {

    private final UserNotificationSettingRepository settingRepository;


    @Transactional
    public void saveNotificationSetting(PrincipalDetails principalDetails, UserNotificationSettingDto.Save dto) {
        User loginUser = principalDetails.getUser();
        UserNotificationSetting setting = UserNotificationSetting.builder()
                .userId(loginUser.getId())
                .hour(dto.hour())
                .minute(dto.minute())
                .dayOfWeek(dto.dayOfWeek())
                .enabled(dto.enabled())
                .build();
        settingRepository.save(setting);
    }
}
