package com.boram.look.api.controller;

import com.boram.look.api.dto.EventTypeDto;
import com.boram.look.api.dto.RegionDto;
import com.boram.look.api.dto.TemperatureRangeDto;
import com.boram.look.service.outfit.OutfitConditionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outfit-condition")
@Slf4j
@RequiredArgsConstructor
public class OutfitConditionController {
    private final OutfitConditionService conditionService;

    @Operation(
            summary = "온도 범위 조회",
            description = "코디 데이터에 사용할 온도 범위 값들을 조회"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 온도 범위 데이터 조회함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TemperatureRangeDto.Copy.class)
            )
    )
    @GetMapping("/temperatures")
    public ResponseEntity<?> getTemperatureRanges(Pageable pageable) {
        Page<TemperatureRangeDto.Copy> pages = conditionService.findTemperatureRanges(pageable);
        return ResponseEntity.ok(pages);
    }

    @Operation(
            summary = "행사 종류 조회",
            description = "코디 데이터에 사용할 행사 종류 값들을 조회"
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 행사 종류 데이터 조회함",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EventTypeDto.Copy.class)
            )
    )
    @GetMapping("/event-types")
    public ResponseEntity<?> getEventTypes(Pageable pageable) {
        Page<EventTypeDto.Copy> pages = conditionService.findEventTypes(pageable);
        return ResponseEntity.ok(pages);
    }

    @Hidden
    @PostMapping("/temperatures")
    public ResponseEntity<?> insertTemperatureRanges(@RequestBody List<TemperatureRangeDto.Save> dtos) {
        conditionService.insertTemperatureRanges(dtos);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @Hidden
    @PostMapping("/event-types")
    public ResponseEntity<?> insertEventTypes(@RequestBody List<EventTypeDto.Save> dtos) {
        conditionService.insertEventTypes(dtos);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @Hidden
    @PutMapping("/event-types")
    public ResponseEntity<?> updateEventTypes(@RequestBody List<EventTypeDto.Copy> dtos) {
        conditionService.updateEventTypes(dtos);
        return ResponseEntity.ok().build();
    }

    @Hidden
    @PutMapping("/temperatures")
    public ResponseEntity<?> updateTemperatureRange(@RequestBody List<TemperatureRangeDto.Copy> dtos) {
        conditionService.updateTemperatureRanges(dtos);
        return ResponseEntity.ok().build();
    }

    @Hidden
    @DeleteMapping("/event-types")
    public ResponseEntity<?> deleteEventTypes(@RequestParam List<Integer> ids) {
        conditionService.deleteEventTypes(ids);
        return ResponseEntity.noContent().build();
    }

    @Hidden
    @DeleteMapping("/temperatures")
    public ResponseEntity<?> deleteTemperatureRanges(@RequestParam List<Integer> dtos) {
        conditionService.deleteTemperatureRanges(dtos);
        return ResponseEntity.noContent().build();
    }


}
