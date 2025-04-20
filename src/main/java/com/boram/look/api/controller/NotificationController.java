package com.boram.look.api.controller;

import com.boram.look.domain.notification.Notification;
import com.boram.look.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class NotificationController {

    private final NotificationService notificationService;


    // 사용자별로 emitter를 보관하는 구조

    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable String userId) {
        log.info("NotificationController.subscribe is called.\nuserId: {}", userId);
        return notificationService.subscribe(userId);
    }

    // 알림 보내기 (관리자나 서버가 호출)
    @PostMapping("/send")
    public void sendNotification(
            @RequestBody Notification notification
    ) {
        log.info("NotificationController.sendNotification is called.\nnotification: {}", notification);
        notificationService.sendNotification(notification);
    }

}
