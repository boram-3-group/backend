package com.boram.look.api.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<?> getHealth() {
        return ResponseEntity.ok("Health Check is OK");
    }
}
