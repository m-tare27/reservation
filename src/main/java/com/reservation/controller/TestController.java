package com.reservation.controller;

import com.reservation.dto.*;
import com.reservation.entity.ReservationStatus;
import com.reservation.service.CancellationPolicyService;
import com.reservation.service.CancellationService;
import com.reservation.service.ExcelService;
import com.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TestController {

//    private final ReservationService reservationService;
//
//    @PostMapping("/reservation")
//    public ResponseEntity<ReservationResponse> createReservation(
//            @Valid @RequestBody ReservationRequest request) {
//
//        ReservationResponse response = reservationService.createReservation(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<Void> updateReservationStatus(
//            @PathVariable Integer id,
//            @RequestBody UpdateReservationStatusRequest request) {
//
//        reservationService.updateReservationStatus(id, request.getStatus());
//        return ResponseEntity.noContent().build();
//    }
//
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ReservationResponse> getReservationById(
//            @PathVariable Integer id) {
//
//        return ResponseEntity.ok(reservationService.getReservationById(id));
//    }
//    private final CancellationPolicyService cancellationPolicyService;
//
//    @PostMapping("/policy")
//    public ResponseEntity<CancellationPolicyResponse> createPolicy(
//            @Valid @RequestBody CancellationPolicyRequest request){
//        CancellationPolicyResponse response = cancellationPolicyService.createCancellationPolicy(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PostMapping("/policy/{id}")
//    public ResponseEntity<CancellationPolicyResponse> updatePolicy(
//            @Valid @RequestBody CancellationPolicyRequest request,
//            @PathVariable Integer id){
//        CancellationPolicyResponse response = cancellationPolicyService.updateCancellationPolicy(id , request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @DeleteMapping("/policy/{id}")
//    public ResponseEntity<Void> deleteCancellationPolicy(@PathVariable Integer id) {
//        cancellationPolicyService.deleteCancellationPolicy(id);
//        return ResponseEntity.noContent().build();
//    }
}
