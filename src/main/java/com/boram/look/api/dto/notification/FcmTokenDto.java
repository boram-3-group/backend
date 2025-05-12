package com.boram.look.api.dto.notification;

public class FcmTokenDto {
    public record Save(
            String fcmToken
    ) {
    }
}
