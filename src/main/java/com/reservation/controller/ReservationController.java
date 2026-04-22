package com.reservation.controller;

import com.reservation.dto.DateRangeRequest;
import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.dto.UpdateReservationStatusRequest;
import com.reservation.entity.ReservationStatus;
import com.reservation.service.ReservationService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @Valid @RequestBody ReservationRequest request , @PathVariable Integer id) {

        ReservationResponse response = reservationService.updateReservation(request , id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateReservationStatus(
            @PathVariable Integer id,
            @RequestBody UpdateReservationStatusRequest request) {

        reservationService.updateReservationStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations(){
        return ResponseEntity.ok(reservationService.getReservation());
    }

    @GetMapping("/bungalow/{id}")
    public ResponseEntity<List<ReservationResponse>> getByBungalowId(@PathVariable Integer id){
        return ResponseEntity.ok(reservationService.getReservationByBungalowId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(@PathVariable Integer id){
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/status")
    public ResponseEntity<List<ReservationResponse>> getByStatus(@RequestParam ReservationStatus status){
        return ResponseEntity.ok(reservationService.getReservationByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ReservationResponse>> getByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                reservationService.getReservationByDateRange(startDate, endDate)
        );
    }

}
