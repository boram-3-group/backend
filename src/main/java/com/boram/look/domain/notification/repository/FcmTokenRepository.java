package com.boram.look.domain.notification.repository;

import com.boram.look.domain.notification.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    List<FcmToken> findByUserId(UUID userId);

    Optional<FcmToken> findByUserIdAndFcmToken(UUID userId, String fcmToken);

}
