package com.boram.look.service.notification;

import com.boram.look.domain.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        // 연결 확인용 dummy 이벤트 전송
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendNotification(Notification notification) {
        SseEmitter emitter = emitters.get(notification.getReceiveUserProfile().getId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(notification.getReceiveUserProfile().getId());
            }
        }
    }


}
