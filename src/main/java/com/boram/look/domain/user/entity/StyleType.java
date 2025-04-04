package com.boram.look.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "style_type")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class StyleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "create_user_id", nullable = false)
    private User createUser;

    @ManyToOne
    @JoinColumn(name = "modify_user_id")
    private User modifyUser;
}
