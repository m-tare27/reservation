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

@RestController
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/test")
    public String testMethod(){
        return "Test Get";
    }

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateReservationStatus(
            @PathVariable Integer id,
            @RequestBody UpdateReservationStatusRequest request) {

        reservationService.updateReservationStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }
}
