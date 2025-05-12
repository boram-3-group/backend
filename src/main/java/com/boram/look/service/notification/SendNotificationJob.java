package com.boram.look.service.notification;

import com.boram.look.api.dto.outfit.OutfitDto;
import com.boram.look.domain.notification.FcmToken;
import com.boram.look.domain.notification.UserNotificationSetting;
import com.boram.look.domain.notification.repository.FcmTokenRepository;
import com.boram.look.domain.notification.repository.UserNotificationSettingRepository;
import com.boram.look.service.outfit.OutfitFacade;
import com.google.firebase.messaging.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SendNotificationJob implements Job {

    private UserNotificationSettingRepository settingRepository;
    private OutfitFacade outfitFacade;
    private FcmTokenRepository fcmTokenRepository;


    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getMergedJobDataMap();
        Long userSettingId = data.getLong("userSettingId");
        Optional<UserNotificationSetting> settingOptional = settingRepository.findById(userSettingId);
        if (settingOptional.isEmpty()) {
            log.debug("user setting is not found");
            return;
        }

        UserNotificationSetting setting = settingOptional.get();
        List<FcmToken> fcmToken = fcmTokenRepository.findByUserId(setting.getUserId());
        if (fcmToken.isEmpty()) {
            log.debug("fcm token is empty");
            return;
        }

        OutfitDto.Transfer transfer = outfitFacade.getOutfitByPosition(
                setting.getLongitude(),
                setting.getLatitude(),
                setting.getEventTypeId(),
                setting.getGender()
        );

        Notification notification = this.buildNotification(transfer, setting);
        List<Message> messages = fcmToken.stream()
                .map(token -> Message.builder()
                        .setToken(token.getFcmToken())
                        .setNotification(notification)
                        .build())
                .toList();


        BatchResponse response = null;
        try {
            response = FirebaseMessaging.getInstance().sendEach(messages);
        } catch (FirebaseMessagingException e) {
            log.error("failed to send notification. setting id: {}", userSettingId);
        }

        assert response != null;
        log.debug("successfully sent message.\n" +
                        "settingId: {}, success count: {}, failure count: {}"
                , userSettingId, response.getSuccessCount(), response.getFailureCount()
        );
    }

    public Notification buildNotification(OutfitDto.Transfer transfer, UserNotificationSetting setting) {
        String title = "오늘의 코디: " + transfer.fileMetadata().getFirst().getTitle();
        String period = setting.getHour() < 12 ? "오전" : "오후";
        int hour12 = setting.getHour() <= 12 ? setting.getHour() : setting.getHour() - 12;
        String content = String.format("""
                오늘은 %s %d시에 %s가 있는 날이에요!
                오늘 날씨와 일정에 어울리는 코디가 준비되어 있어요.
                확인해 보실래요?
                """, period, hour12, transfer.eventType());

        return Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();
    }


    @Autowired
    public void setSettingRepository(UserNotificationSettingRepository repo) {
        this.settingRepository = repo;
    }

    @Autowired
    public void setOutfitFacade(OutfitFacade facade) {
        this.outfitFacade = facade;
    }

    @Autowired
    public void setFcmTokenRepository(FcmTokenRepository repo) {
        this.fcmTokenRepository = repo;
    }
}
