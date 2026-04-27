package com.reservation.service;

import com.reservation.dto.PaymentRequest;
import com.reservation.dto.PaymentResponse;
import com.reservation.entity.Payment;
import com.reservation.entity.PaymentStatus;
import com.reservation.entity.Reservation;
import com.reservation.repository.PaymentRepository;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private static final double TOLERANCE = 0.01;

    public PaymentResponse processPayment(PaymentRequest request) {

        validatePaymentRequest(request);

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + request.getReservationId() + " not found"));

        Double totalPaid = paymentRepository.sumCompletedPaymentsByReservationId(reservation.getId());
        if (totalPaid == null) totalPaid = 0.0;

        if (totalPaid + request.getAmount() > reservation.getTotalAmount() + TOLERANCE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Payment amount %.2f exceeds remaining balance %.2f",
                            request.getAmount(),
                            reservation.getTotalAmount() - totalPaid));
        }

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setReservation(reservation);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);
        return new PaymentResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment with id " + paymentId + " not found"));

        return new PaymentResponse(payment);
    }

    public PaymentResponse completePayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment with id " + paymentId + " not found"));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment is already completed");
        }
        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refunded payments cannot be completed");
        }

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(savedPayment);
    }

    public PaymentResponse refundPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment with id " + paymentId + " not found"));

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only completed payments can be refunded");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(savedPayment);
    }
    //Helper Methods

    private void validatePaymentRequest(PaymentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment request is required");
        }

        if (request.getReservationId() == null || request.getReservationId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid reservation ID is required");
        }

        if (request.getAmount() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount must be greater than 0");
        }

        if (request.getAmount() > 1_000_000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount exceeds maximum allowed");
        }
    }
}
