package com.boram.look.api.dto;

import com.boram.look.domain.condition.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class EventTypeDto {

    @Schema(name = "EventTypeDto.Save", description = "일정 타입 삽입 DTO")
    public record Save(
            @Schema(description = "타입 이름")
            String categoryName
    ) {
        public EventType toEntity() {
            return EventType.builder().categoryName(this.categoryName()).build();
        }
    }

    @Builder
    @Schema(name = "EventTypeDto.Copy", description = "조회, 수정시 사용")
    public record Copy(
            @Schema(description = "id")
            Integer id,
            @Schema(description = "타입 이름")
            String categoryName
    ) {
    }

}
