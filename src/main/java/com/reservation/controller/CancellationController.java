package com.reservation.controller;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.entity.RefundStatus;
import com.reservation.service.CancellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cancel")
@RequiredArgsConstructor
public class CancellationController {
    private final CancellationService cancellationService;

    @PostMapping
    public CancellationResponse cancelReservation(@RequestBody CancellationRequest request){
        return cancellationService.cancelReservation(request);
    }

    @GetMapping
    public ResponseEntity<List<CancellationResponse>> getAll(){
        return ResponseEntity.ok(cancellationService.getAllCancellation());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CancellationResponse> getById(@PathVariable Integer id){
        return ResponseEntity.ok(cancellationService.getCancellationById(id));
    }

    @GetMapping("/refund-status")
    public ResponseEntity<List<CancellationResponse>> getByRefundStatus(@RequestParam RefundStatus status){
        return ResponseEntity.ok(cancellationService.getAllCancellationByRefundStatus(status));
    }

    @GetMapping("/cancelled-at")
    public ResponseEntity<List<CancellationResponse>> getByCancelledAt(@RequestParam LocalDateTime time){
        return ResponseEntity.ok(cancellationService.getAllCancellationByCancelledAt(time));
    }

    @GetMapping("/days-before-check-in/{days}")
    public ResponseEntity<List<CancellationResponse>> getByDaysBeforeCheckIn(@PathVariable long days){
        return ResponseEntity.ok(cancellationService.getAllCancellationByDaysBeforeCheckIn(days));
    }

}
