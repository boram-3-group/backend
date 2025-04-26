package com.boram.look.api.controller;

import com.boram.look.service.InitService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@Slf4j
@RequiredArgsConstructor
public class HealthController {

    private final InitService initService;

    @GetMapping("/")
    public ResponseEntity<?> getHealth() {
        return ResponseEntity.ok("Health Check is OK");
    }

    @GetMapping("/refresh-cache")
    public ResponseEntity<?> refreshCache() {
        initService.initServer();
        return ResponseEntity.ok().build();
    }

}
