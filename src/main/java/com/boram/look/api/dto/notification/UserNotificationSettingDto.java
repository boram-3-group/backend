package com.boram.look.api.dto.notification;

import com.boram.look.domain.notification.NotificationDayOfWeek;
import com.boram.look.domain.user.constants.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class UserNotificationSettingDto {
    @Schema(name = "UserNotificationSettingDto.Save", description = "유저 알림 세팅 저장 DTO")
    public record Save(
            @Schema(description = "원하는 시간", example = "23")
            Integer hour,
            @Schema(description = "원하는 분", example = "23")
            Integer minute,
            @Schema(description = "원하는 요일, 매일을 원하는 경우 EVERYDAY", example = "EVERYDAY")
            NotificationDayOfWeek dayOfWeek,
            @Schema(description = "사용여부", example = "true")
            Boolean enabled,
            @Schema(description = "현재 사용자의 위도", example = "37.5665")
            Float latitude,
            @Schema(description = "현재 사용자의 경도", example = "126.978")
            Float longitude,
            @Schema(description = "알림 받고 싶은 일정의 id", example = "1")
            Integer eventTypeId,
            @Schema(description = "성별", example = "MALE")
            Gender gender
    ) {
    }

    @Builder
    @Schema(name = "UserNotificationSettingDto.Get", description = "유저 알림 세팅 조회 DTO")
    public record Get(
            @Schema(description = "id", example = "1")
            Long id,
            @Schema(description = "원하는 시간", example = "23")
            Integer hour,
            @Schema(description = "원하는 분", example = "23")
            Integer minute,
            @Schema(description = "원하는 요일, 매일을 원하는 경우 EVERYDAY", example = "EVERYDAY")
            NotificationDayOfWeek dayOfWeek,
            @Schema(description = "사용여부", example = "true")
            Boolean enabled,
            @Schema(description = "현재 사용자의 위도", example = "37.5665")
            Float latitude,
            @Schema(description = "현재 사용자의 경도", example = "126.978")
            Float longitude,
            @Schema(description = "알림 받고 싶은 일정의 id", example = "1")
            Integer eventTypeId,
            @Schema(description = "성별", example = "MALE")
            Gender gender
    ) {
    }

}
