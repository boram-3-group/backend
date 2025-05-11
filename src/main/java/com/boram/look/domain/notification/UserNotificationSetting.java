package com.boram.look.domain.notification;

import com.boram.look.domain.AuditingFields;
import com.boram.look.domain.user.constants.Gender;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
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
    private Gender gender;
}
