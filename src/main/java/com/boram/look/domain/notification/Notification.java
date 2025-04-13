package com.boram.look.domain.notification;

import com.boram.look.api.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Notification {
    private UserDto.Profile sendUserProfile;
    private UserDto.Profile receiveUserProfile;
    private String content;
    private String redirectUrl;
    private NotificationType notificationType;
}
