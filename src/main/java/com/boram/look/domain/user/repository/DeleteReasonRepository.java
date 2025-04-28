package com.boram.look.domain.user.repository;

import com.boram.look.domain.user.entity.DeleteReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteReasonRepository extends JpaRepository<DeleteReason, Long> {
}
