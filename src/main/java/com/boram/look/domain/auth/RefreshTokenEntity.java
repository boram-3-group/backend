package com.boram.look.domain.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Getter
@ToString
public class RefreshTokenEntity {
    @Id
    private String deviceId;
    private String refreshTokenValue;
    private String userId;
    private String roleString;

    public void update(String newRefreshToken) {
        this.refreshTokenValue = newRefreshToken;
    }
}
