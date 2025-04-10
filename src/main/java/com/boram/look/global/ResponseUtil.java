package com.boram.look.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<?> buildUnauthorizedResponseEntity(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}
