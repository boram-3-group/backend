package com.boram.look.api.controller;

import com.boram.look.api.dto.EventTypeDto;
import com.boram.look.api.dto.TemperatureRangeDto;
import com.boram.look.service.outfit.OutfitConditionService;
import com.boram.look.service.s3.FileFacade;
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

    @GetMapping("/temperatures")
    public ResponseEntity<?> getTemperatureRanges(Pageable pageable) {
        Page<TemperatureRangeDto.Copy> pages = conditionService.findTemperatureRanges(pageable);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/event-types")
    public ResponseEntity<?> getEventTypes(Pageable pageable) {
        Page<EventTypeDto.Copy> pages = conditionService.findEventTypes(pageable);
        return ResponseEntity.ok(pages);
    }

    @PostMapping("/temperatures")
    public ResponseEntity<?> insertTemperatureRanges(@RequestBody List<TemperatureRangeDto.Save> dtos) {
        conditionService.insertTemperatureRanges(dtos);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PostMapping("/event-types")
    public ResponseEntity<?> insertEventTypes(@RequestBody List<EventTypeDto.Save> dtos) {
        conditionService.insertEventTypes(dtos);
        return ResponseEntity.created(URI.create("asdf")).build();
    }

    @PutMapping("/event-types")
    public ResponseEntity<?> updateEventTypes(@RequestBody List<EventTypeDto.Copy> dtos) {
        conditionService.updateEventTypes(dtos);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/temperatures")
    public ResponseEntity<?> updateTemperatureRange(@RequestBody List<TemperatureRangeDto.Copy> dtos) {
        conditionService.updateTemperatureRanges(dtos);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/event-types")
    public ResponseEntity<?> deleteEventTypes(@RequestParam List<Integer> ids) {
        conditionService.deleteEventTypes(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/temperatures")
    public ResponseEntity<?> deleteTemperatureRanges(@RequestParam List<Integer> dtos) {
        conditionService.deleteTemperatureRanges(dtos);
        return ResponseEntity.noContent().build();
    }


}
