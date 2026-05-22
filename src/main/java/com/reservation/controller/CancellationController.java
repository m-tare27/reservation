package com.reservation.controller;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.enums.RefundStatus;
import com.reservation.service.CancellationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cancellation API", description = "APIs for managing reservation cancellations")
public class CancellationController {
    private final CancellationService cancellationService;

    @PostMapping
    @Operation(summary = "Cancel a reservation", description = "Cancels a reservation and processes refund if applicable")
    public ResponseEntity<CancellationResponse> cancelReservation(
            @Valid @RequestBody CancellationRequest request) {

        CancellationResponse response = cancellationService.cancelReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get cancellations", description = "Retrieves a list of cancellations with optional filters")
    public ResponseEntity<List<CancellationResponse>> getCancellations(
            @RequestParam(required = false) Integer cancellationId,
            @RequestParam(required = false) Integer reservationId,
            @RequestParam(required = false) Integer cancellationPolicyId,
            @RequestParam(required = false) RefundStatus refundStatus) {

        List<CancellationResponse> response = cancellationService.getCancellations(
                cancellationId , refundStatus , reservationId , cancellationPolicyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cancellation by ID", description = "Retrieves cancellation details by its ID")
    public ResponseEntity<CancellationResponse> getCancellationById(@PathVariable Integer id) {
        CancellationResponse response = cancellationService.getCancellationById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/refund-status/{id}")
    @Operation(summary = "Update refund status", description = "Updates the refund status of a cancellation")
    public ResponseEntity<CancellationResponse> updateRefundStatus(
            @PathVariable Integer id,
            @RequestParam RefundStatus refundStatus) {

        CancellationResponse response = cancellationService.updateRefundStatus(id, refundStatus);
        return ResponseEntity.ok(response);
    }
}
