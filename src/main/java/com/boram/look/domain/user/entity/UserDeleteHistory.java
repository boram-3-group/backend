package com.boram.look.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "user_delete_history")
public class UserDeleteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "username")
    private String username;
    @ManyToOne
    @JoinColumn(name = "delete_reason_id")
    private DeleteReason deleteReason;

}
