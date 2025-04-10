package com.boram.look.service.notification;

import com.boram.look.api.dto.NotificationDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(
            NotificationDto dto
    ) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(dto.getTitle())
                .setBody(dto.toString())
                .build();
        Message message = Message.builder()
                .setToken(dto.getTargetToken())
                .setNotification(notification)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }

}
