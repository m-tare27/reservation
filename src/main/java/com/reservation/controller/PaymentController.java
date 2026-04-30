package com.reservation.controller;

import com.reservation.dto.PaymentRequest;
import com.reservation.dto.PaymentResponse;
import com.reservation.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentByReservationId(@PathVariable Integer reservationId) {
        List<PaymentResponse> response = paymentService.getPaymentsByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paymentId}/complete")
    public ResponseEntity<PaymentResponse> completePayment(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.completePayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponse> failPayment(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.failPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bungalow/{bungalowId}/revenue")
    public ResponseEntity<Double> getRevenueByBungalowId(@PathVariable Integer bungalowId) {
        Double revenue = paymentService.getRevenueByBungalowId(bungalowId);
        return ResponseEntity.ok(revenue);
    }
}
