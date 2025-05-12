package com.boram.look.domain.notification.repository;

import com.boram.look.domain.notification.UserNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
    Optional<UserNotificationSetting> findByUserId(UUID userId);
}
