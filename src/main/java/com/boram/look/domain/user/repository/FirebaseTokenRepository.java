package com.boram.look.domain.user.repository;

import com.boram.look.domain.user.entity.FirebaseToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, Long> {
}
