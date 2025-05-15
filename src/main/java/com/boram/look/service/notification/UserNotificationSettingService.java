package com.boram.look.service.notification;

import com.boram.look.api.dto.notification.NotificationDto;
import com.boram.look.api.dto.notification.UserNotificationSettingDto;
import com.boram.look.domain.notification.UserNotificationSetting;
import com.boram.look.domain.notification.repository.UserNotificationSettingRepository;
import com.boram.look.domain.user.entity.User;
import com.boram.look.global.security.authentication.PrincipalDetails;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationSettingService {

    private final UserNotificationSettingRepository settingRepository;
    private final NotificationSchedulerService schedulerService;

    @Transactional
    public void saveNotificationSetting(PrincipalDetails principalDetails, UserNotificationSettingDto.Save dto) {
        User loginUser = principalDetails.getUser();
        UserNotificationSetting setting = settingRepository.findByUserId(loginUser.getId())
                .map(existing -> {
                    existing.update(dto);
                    return existing;
                })
                .orElseGet(() -> UserNotificationSetting.builder()
                        .userId(loginUser.getId())
                        .hour(dto.hour())
                        .minute(dto.minute())
                        .dayOfWeek(dto.dayOfWeek())
                        .enabled(dto.enabled())
                        .latitude(dto.latitude())
                        .longitude(dto.longitude())
                        .eventTypeId(dto.eventTypeId())
                        .gender(dto.gender())
                        .build());

        UserNotificationSetting entity = settingRepository.save(setting);
        // 유저의 이전 알림 세팅을 삭제한 후 재등록
        schedulerService.deleteUserNotification(setting.getId());
        schedulerService.scheduleUserNotification(entity);
    }

    @Transactional
    public void deleteNotificationSetting(PrincipalDetails principalDetails) {
        User loginUser = principalDetails.getUser();
        UserNotificationSetting setting = settingRepository.findByUserId(loginUser.getId())
                .orElseThrow(EntityNotFoundException::new);
        schedulerService.deleteUserNotification(setting.getId());
        settingRepository.delete(setting);
    }

    public void sendNotification(NotificationDto dto) {
        Notification notification = Notification.builder()
                .setTitle(dto.getTitle())
                .setBody(dto.toString())
                .build();
        Message message = Message.builder()
                .setToken(dto.getTargetToken())
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: " + response);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Transactional(readOnly = true)
    public void loadSetting() {
        List<UserNotificationSetting> settings = settingRepository.findAll();
        settings.forEach(schedulerService::scheduleUserNotification);
    }

}
