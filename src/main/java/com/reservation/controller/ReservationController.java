package com.reservation.controller;

import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.enums.ReservationStatus;
import com.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservation API", description = "APIs for managing reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Creates a new reservation with the provided details.")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reservation", description = "Updates the reservation with the specified ID using the provided details.")
    public ResponseEntity<ReservationResponse> updateReservation(
            @Valid @RequestBody ReservationRequest request, @PathVariable Integer id) {

        ReservationResponse response = reservationService.updateReservation(request, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm reservation status", description = "Confirms the status of the reservation with the specified ID.")
    public ResponseEntity<Void> updateReservationStatus(
            @PathVariable Integer id,
            @RequestParam ReservationStatus status) {

        reservationService.confirmReservationStatus(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get reservations with optional filters", description = "Retrieves a list of reservations based on the provided optional filters such as ID, bungalow ID, status, and date range.")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer bungalowId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<ReservationResponse> response = reservationService.getReservations(id, bungalowId, status, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/complete")
    @Operation(summary = "Complete a reservation", description = "Marks the reservation with the specified ID as completed.")
    public ResponseEntity<Void> completeReservation(@RequestParam Integer reservationId) {
        reservationService.completeReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

}
