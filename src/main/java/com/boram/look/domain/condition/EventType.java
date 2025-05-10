package com.boram.look.domain.condition;

import com.boram.look.api.dto.outfit.EventTypeDto;
import com.boram.look.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "event_type")
public class EventType extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "category_name", nullable = false)
    private String categoryName;
    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    public void update(String categoryName) {
        this.categoryName = categoryName;
    }

    public EventTypeDto.Copy toDto() {
        return EventTypeDto.Copy.builder()
                .id(this.id)
                .categoryName(this.categoryName)
                .sequence(this.sequence)
                .build();
    }
}
