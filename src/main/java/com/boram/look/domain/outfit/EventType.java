package com.boram.look.domain.outfit;

import com.boram.look.api.dto.EventTypeDto;
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
    private String categoryName;

    public void update(String categoryName) {
        this.categoryName = categoryName;
    }

    public EventTypeDto.Copy toDto() {
        return EventTypeDto.Copy.builder()
                .id(this.id)
                .categoryName(this.categoryName)
                .build();
    }
}
