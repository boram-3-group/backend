package com.boram.look.api.controller;

import com.boram.look.api.dto.NotificationDto;
import com.boram.look.service.notification.NotificationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 보내기 (관리자나 서버가 호출)
    @PostMapping("/send")
    public void sendNotification(
            @RequestBody NotificationDto notificationDto
    ) {
        log.info("NotificationController.sendNotification is called.\nnotification: {}", notificationDto);
        try {
            notificationService.sendNotification(notificationDto);
        } catch (FirebaseMessagingException e) {
            log.error("firebase messaging ex.\n{}", e.getMessage());
        }
    }

}
