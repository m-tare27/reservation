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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private static final BigDecimal TOLERANCE =
            new BigDecimal("0.01");
    private static final BigDecimal MAX_PAYMENT_AMOUNT =
            new BigDecimal("100000.00");

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

        BigDecimal totalPaid =
                paymentRepository.sumPaymentsByReservationId(reservation.getId());

        BigDecimal updatedTotal =
                totalPaid.add(request.getAmount());

        BigDecimal allowedTotal =
                reservation.getTotalAmount().add(TOLERANCE);

        if (updatedTotal.compareTo(allowedTotal) > 0) {

            BigDecimal remainingBalance =
                    reservation.getTotalAmount().subtract(totalPaid);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "Payment amount %s exceeds remaining balance %s",
                            request.getAmount(),
                            remainingBalance
                    )
            );
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

        BigDecimal refundAmount = reservation.getCancellation().getRefundAmount();

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

    public BigDecimal getRevenueByBungalowId(Integer bungalowId) {

        BigDecimal revenue =
                paymentRepository.getRevenueByBungalowId(bungalowId);

        return revenue != null
                ? revenue
                : BigDecimal.ZERO;
    }

    public void cancelPayments(Integer reservationId){
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);
        payments.forEach(
                payment -> payment.setPaymentStatus(PaymentStatus.REFUNDED)
        );
    }

    //Helper Methods
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
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount must be greater than 0"
            );
        }

        if (request.getAmount().compareTo(MAX_PAYMENT_AMOUNT) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount exceeds maximum allowed"
            );
        }
    }
}
