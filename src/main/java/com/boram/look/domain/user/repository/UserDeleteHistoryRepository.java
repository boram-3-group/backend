package com.boram.look.domain.user.repository;

import com.boram.look.domain.user.entity.UserDeleteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeleteHistoryRepository extends JpaRepository<UserDeleteHistory, Long> {
}
