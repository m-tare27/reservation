package com.reservation.controller;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.entity.RefundStatus;
import com.reservation.service.CancellationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cancellations")
@RequiredArgsConstructor
public class CancellationController {
    private final CancellationService cancellationService;

    @PostMapping
    public ResponseEntity<CancellationResponse> cancelReservation(
            @Valid @RequestBody CancellationRequest request) {

        CancellationResponse response = cancellationService.cancelReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CancellationResponse>> getCancellations(
            @RequestParam(required = false) LocalDateTime cancelledAt,
            @RequestParam(required = false) Long daysBeforeCheckIn,
            @RequestParam(required = false) RefundStatus refundStatus) {

        List<CancellationResponse> response = cancellationService.getCancellations(
                cancelledAt, daysBeforeCheckIn, refundStatus);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CancellationResponse> getCancellationById(@PathVariable Integer id) {
        CancellationResponse response = cancellationService.getCancellationById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/refund-status/{id}")
    public ResponseEntity<CancellationResponse> updateRefundStatus(
            @PathVariable Integer id,
            @RequestParam RefundStatus refundStatus) {

        CancellationResponse response = cancellationService.updateRefundStatus(id, refundStatus);
        return ResponseEntity.ok(response);
    }
}
