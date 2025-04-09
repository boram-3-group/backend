package com.boram.look.domain.auth.repository;

import com.boram.look.domain.auth.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByUserIdAndDeviceId(String userId, String deviceId);

}
