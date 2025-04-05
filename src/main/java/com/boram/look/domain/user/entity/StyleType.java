package com.boram.look.domain.user.entity;

import com.boram.look.api.dto.StyleTypeDto;
import com.boram.look.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "style_type")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class StyleType extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content", length = 500, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "create_user_id", nullable = false)
    private User createUser;

    @ManyToOne
    @JoinColumn(name = "update_user_id")
    private User updateUser;

    public StyleType(String content) {
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }

    public StyleTypeDto.Get toDto() {
        return StyleTypeDto.Get.builder()
                .id(this.id)
                .content(this.content)
                .createUser(this.createUser.toDto())
                .updateUser(this.updateUser.toDto())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}
