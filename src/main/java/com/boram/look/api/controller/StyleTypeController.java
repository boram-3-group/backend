package com.boram.look.api.controller;

import com.boram.look.api.dto.StyleTypeDto;
import com.boram.look.service.user.StyleTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/style-type")
@RequiredArgsConstructor
@Slf4j
public class StyleTypeController {

    private final StyleTypeService styleTypeService;

    @PostMapping
    public ResponseEntity<?> doSensitivityEdit(@RequestBody List<StyleTypeDto.Edit> dto) {
        styleTypeService.doEdit(dto);
        return ResponseEntity.ok("편집 완료");
    }

    @GetMapping
    public ResponseEntity<?> getStyleTypes(Pageable pageable) {
        Page<StyleTypeDto.Get> pages = styleTypeService.getStyleTypes(pageable);
        return ResponseEntity.ok(pages);
    }

}
