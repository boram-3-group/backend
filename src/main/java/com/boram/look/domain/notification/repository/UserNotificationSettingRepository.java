package com.boram.look.domain.notification.repository;

import com.boram.look.domain.notification.UserNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {
}
