package com.boram.look.api.dto.notification;

import com.boram.look.domain.notification.NotificationDayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserNotificationSettingDto {
    @Schema(name = "UserNotificationSettingDto.Save", description = "유저 알림 세팅 저장 DTO")
    public record Save(
            @Schema(name = "원하는 시간", example = "23")
            Integer hour,
            @Schema(name = "원하는 분", example = "23")
            Integer minute,
            @Schema(name = "원하는 요일", description = "매일을 원하는 경우 EVERYDAY", example = "EVERYDAY")
            NotificationDayOfWeek dayOfWeek,
            @Schema(name = "사용여부", example = "true")
            Boolean enabled,
            @Schema(name = "현재 사용자의 위도")
            Float latitude,
            @Schema(name = "현재 사용자의 경도")
            Float longitude,
            @Schema(name = "알림 받고 싶은 일정의 id")
            Integer eventTypeId
    ) {
    }

}
