package com.reservation.controller;

import com.reservation.dto.PaymentRequest;
import com.reservation.dto.PaymentResponse;
import com.reservation.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "APIs for processing and managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process a new payment", description = "Processes a new payment for a reservation using the provided payment details.")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details by ID", description = "Retrieves the details of a payment using its unique ID.")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get payments by reservation ID", description = "Retrieves a list of payments associated with a specific reservation ID.")
    public ResponseEntity<List<PaymentResponse>> getPaymentByReservationId(@PathVariable Integer reservationId) {
        List<PaymentResponse> response = paymentService.getPaymentsByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paymentId}/complete")
    @Operation(summary = "Complete a payment", description = "Marks the payment with the specified ID as completed.")
    public ResponseEntity<PaymentResponse> completePayment(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.completePayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paymentId}/refund")
    @Operation(summary = "Refund a payment", description = "Marks the payment with the specified ID as refunded.")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{paymentId}/fail")
    @Operation(summary = "Fail a payment", description = "Marks the payment with the specified ID as failed.")
    public ResponseEntity<PaymentResponse> failPayment(@PathVariable Integer paymentId) {
        PaymentResponse response = paymentService.failPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bungalow/{bungalowId}/revenue")
    @Operation(summary = "Get revenue by bungalow ID", description = "Retrieves the total revenue generated from payments associated with a specific bungalow ID.")
    public ResponseEntity<Double> getRevenueByBungalowId(@PathVariable Integer bungalowId) {
        Double revenue = paymentService.getRevenueByBungalowId(bungalowId);
        return ResponseEntity.ok(revenue);
    }
}
