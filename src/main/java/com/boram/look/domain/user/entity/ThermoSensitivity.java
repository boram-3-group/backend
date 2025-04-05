package com.boram.look.domain.user.entity;

import com.boram.look.api.dto.SensitivityDto;
import com.boram.look.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "body_type")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class ThermoSensitivity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "create_user_id", nullable = false)
    private User createUser;

    @ManyToOne
    @JoinColumn(name = "update_user_id")
    private User updateUser;

    public ThermoSensitivity(String content) {
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public SensitivityDto.Get toDto() {
        return SensitivityDto.Get.builder()
                .id(this.id)
                .content(this.content)
                .createUser(this.createUser.toDto())
                .updateUser(this.updateUser.toDto())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}
