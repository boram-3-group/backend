package com.boram.look.domain.auth;

import jakarta.persistence.Column;
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
    @Column(name = "refresh_token_value", nullable = false, length = 4000)
    private String refreshTokenValue;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "role_string")
    private String roleString;

    public void update(String newRefreshToken) {
        this.refreshTokenValue = newRefreshToken;
    }
}
