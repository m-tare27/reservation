package com.reservation.service;

import com.reservation.dto.PaymentRequest;
import com.reservation.dto.PaymentResponse;
import com.reservation.entity.Payment;
import com.reservation.enums.PaymentStatus;
import com.reservation.entity.Reservation;
import com.reservation.enums.ReservationStatus;
import com.reservation.repository.PaymentRepository;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private static final double TOLERANCE = 0.01;
    private static final Set<ReservationStatus> PAYABLE_STATUSES = Set.of(
            ReservationStatus.PENDING,
            ReservationStatus.CONFIRMED
    );

    public PaymentResponse processPayment(PaymentRequest request) {

        validatePaymentRequest(request);

        Reservation reservation = reservationRepository.findByIdForUpdate(request.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + request.getReservationId() + " not found"));

        if (!PAYABLE_STATUSES.contains(reservation.getReservationStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment cannot be processed for reservation status: "
                            + reservation.getReservationStatus()
            );
        }

        Double totalPaid = paymentRepository.sumPaymentsByReservationId(reservation.getId());
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
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

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

    public void refundPayment(Integer reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + reservationId + " not found"));

        Double refundAmount = reservation.getCancellation().getRefundAmount();

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setAmount(refundAmount);
        payment.setReservation(reservation);
        paymentRepository.save(payment);
    }

    public List<PaymentResponse> getPaymentsByReservationId(Integer reservationId) {
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);
        return payments.stream()
                .map(PaymentResponse::new)
                .toList();
    }

    public Double getRevenueByBungalowId(Integer bungalowId) {
        Double revenue = paymentRepository.getRevenueByBungalowId(bungalowId);
        return revenue != null ? revenue : 0.0;
    }

    public void cancelPayments(Integer reservationId){
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);
        payments.forEach(
                payment -> payment.setPaymentStatus(PaymentStatus.CANCELLED)
        );
    }

    //Helper Methods

    private static final double MAX_PAYMENT_AMOUNT = 1_000_000;

    private void validatePaymentRequest(PaymentRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment request is required"
            );
        }

        if (request.getReservationId() == null ||
                request.getReservationId() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid reservation ID is required"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount must be greater than 0"
            );
        }

        if (request.getAmount() > MAX_PAYMENT_AMOUNT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount exceeds maximum allowed"
            );
        }
    }
}
