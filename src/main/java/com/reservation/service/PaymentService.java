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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private static final double TOLERANCE = 0.01;

    public PaymentResponse processPayment(PaymentRequest request) {

        validatePaymentRequest(request);

        Reservation reservation = reservationRepository.findByIdForUpdate(request.getReservationId())
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

        Reservation reservation = reservationRepository
                .findByIdForUpdate(payment.getReservation().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"));

        switch (payment.getPaymentStatus()) {

            case PENDING -> {
                Double totalPaid = paymentRepository
                        .sumCompletedPaymentsByReservationId(reservation.getId());

                if (totalPaid == null) totalPaid = 0.0;

                if (totalPaid + payment.getAmount() > reservation.getTotalAmount() + TOLERANCE) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Completing this payment would exceed total amount");
                }

                payment.setPaymentStatus(PaymentStatus.COMPLETED);
            }

            case COMPLETED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment is already completed");

            case REFUNDED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refunded payments cannot be completed");

            case FAILED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed payments cannot be completed");
        }

        Payment savedPayment = paymentRepository.save(payment);

        Double totalPaid = paymentRepository
                .sumCompletedPaymentsByReservationId(reservation.getId());

        if (totalPaid == null) totalPaid = 0.0;

        if (totalPaid >= reservation.getTotalAmount() - TOLERANCE) {
                paymentRepository.findByReservationIdAndStatus(reservation.getId(), PaymentStatus.PENDING)
                        .forEach(p -> {
                            p.setPaymentStatus(PaymentStatus.CANCELLED);
                            paymentRepository.save(p);
                        });
            }

        return new PaymentResponse(savedPayment);
    }

    public PaymentResponse refundPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment with id " + paymentId + " not found"));

        switch (payment.getPaymentStatus()) {

            case COMPLETED -> payment.setPaymentStatus(PaymentStatus.REFUNDED);

            case PENDING, FAILED , CANCELLED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only completed payments can be refunded");

            case REFUNDED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already refunded");
        }
        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(savedPayment);
    }

    public List<PaymentResponse> getPaymentsByReservationId(Integer reservationId) {
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);
        return payments.stream()
                .map(PaymentResponse::new)
                .toList();
    }

    public PaymentResponse failPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending payments can fail");
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        return new PaymentResponse(paymentRepository.save(payment));
    }

    public Double getRevenueByBungalowId(Integer bungalowId) {
        Double revenue = paymentRepository.getRevenueByBungalowId(bungalowId);
        return revenue != null ? revenue : 0.0;
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

        if (request.getAmount() == null || request.getAmount() <= 0) {
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
