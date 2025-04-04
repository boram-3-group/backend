package com.boram.look.domain.user.entity;

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
    @JoinColumn(name = "modify_user_id")
    private User modifyUser;
}
