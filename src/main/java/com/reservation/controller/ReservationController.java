package com.reservation.controller;

import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.dto.UpdateReservationStatusRequest;
import com.reservation.entity.ReservationStatus;
import com.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @Valid @RequestBody ReservationRequest request , @PathVariable Integer id) {

        ReservationResponse response = reservationService.updateReservation(request , id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateReservationStatus(
            @PathVariable Integer id,
            @RequestParam ReservationStatus status) {

        reservationService.updateReservationStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer bungalowId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<ReservationResponse> response = reservationService.getReservations(id, bungalowId, status, startDate, endDate);
        return ResponseEntity.ok(response);
    }

}
