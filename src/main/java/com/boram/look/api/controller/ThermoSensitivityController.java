package com.boram.look.api.controller;

import com.boram.look.api.dto.SensitivityDto;
import com.boram.look.api.dto.StyleTypeDto;
import com.boram.look.service.user.ThermoSensitivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensitivity")
@RequiredArgsConstructor
@Slf4j
public class ThermoSensitivityController {

    private final ThermoSensitivityService sensitivityService;

    @PostMapping
    public ResponseEntity<?> doSensitivityEdit(@RequestBody List<SensitivityDto.Edit> dto) {
        sensitivityService.doEdit(dto);
        return ResponseEntity.ok("편집 완료");
    }

    @GetMapping
    public ResponseEntity<?> getThermoSensitivities(Pageable pageable) {
        Page<SensitivityDto.Get> pages = sensitivityService.getThermoSensitivities(pageable);
        return ResponseEntity.ok(pages);
    }


}
