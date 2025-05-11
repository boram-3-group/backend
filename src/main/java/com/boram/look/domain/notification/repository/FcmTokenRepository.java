package com.boram.look.domain.notification.repository;

import com.boram.look.domain.notification.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
}
