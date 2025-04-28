package com.boram.look.domain.user.entity;

import com.boram.look.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "delete_reason")
@ToString
@Getter
public class DeleteReason extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

}
