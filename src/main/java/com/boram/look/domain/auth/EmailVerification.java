package com.boram.look.domain.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class EmailVerification {
    private String email;

    private String code;

    private Duration expiresAt = Duration.ofMinutes(3);

    private boolean verified;
}
