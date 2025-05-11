package com.boram.look.api.dto.notification;

import com.boram.look.domain.notification.NotificationDayOfWeek;

public class UserNotificationSettingDto {
    public record Save(
            Integer hour,
            Integer minute,
            NotificationDayOfWeek dayOfWeek,
            Boolean enabled,
            Float latitude,
            Float longitude,
            Integer eventTypeId
    ) {
    }

}
