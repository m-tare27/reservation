package com.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
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
