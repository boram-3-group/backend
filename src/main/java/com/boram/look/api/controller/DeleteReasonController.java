package com.boram.look.api.controller;

import com.boram.look.api.dto.DeleteReasonDto;
import com.boram.look.domain.user.entity.DeleteReason;
import com.boram.look.service.user.DeleteReasonService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delete-reason")
@Slf4j
public class DeleteReasonController {

    private final DeleteReasonService deleteReasonService;

    @GetMapping
    public ResponseEntity<?> getAllDeleteReason() {
        List<DeleteReasonDto.Get> reasons = deleteReasonService.findAllDeleteReason();
        return ResponseEntity.ok(reasons);
    }

}
