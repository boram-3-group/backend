package com.boram.look.api.dto;


import lombok.Builder;

public class DeleteReasonDto {

    @Builder
    public record Get(
            Long id,
            String description
    ) {
    }
}
