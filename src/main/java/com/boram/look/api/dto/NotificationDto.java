package com.boram.look.api.dto;

import com.boram.look.domain.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotificationDto {
    private UserDto.Profile sendUserProfile;
    private UserDto.Profile receiveUserProfile;
    private String title;
    private String content;
    private String redirectUrl;
    private NotificationType notificationType;
    private String targetToken;
}
