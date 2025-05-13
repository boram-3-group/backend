package com.boram.look.domain.notification;

import com.boram.look.api.dto.notification.UserNotificationSettingDto;
import com.boram.look.domain.AuditingFields;
import com.boram.look.domain.user.constants.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_notification_setting")
@ToString
@Entity
public class UserNotificationSetting extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 유저 탈퇴시 알림 세팅, fcm-token 지워야 함.
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false, unique = true)
    private UUID userId;

    private Integer hour;
    private Integer minute;
    @Column(name = "day_of_week", length = 10)
    @Enumerated(EnumType.STRING)
    private NotificationDayOfWeek dayOfWeek;

    private Boolean enabled;

    private Float latitude;
    private Float longitude;
    private Integer eventTypeId;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public void update(UserNotificationSettingDto.Save dto) {
        this.hour = dto.hour();
        this.minute = dto.minute();
        this.dayOfWeek = dto.dayOfWeek();
        this.enabled = dto.enabled();
        this.latitude = dto.latitude();
        this.longitude = dto.longitude();
        this.eventTypeId = dto.eventTypeId();
        this.gender = dto.gender();
    }
}
