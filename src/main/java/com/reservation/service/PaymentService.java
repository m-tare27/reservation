package com.reservation.service;

import com.reservation.dto.PaymentRequest;
import com.reservation.dto.PaymentResponse;
import com.reservation.entity.Payment;
import com.reservation.entity.PaymentStatus;
import com.reservation.entity.Reservation;
import com.reservation.repository.PaymentRepository;
import com.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentResponse processPayment(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Invalid Reservation Id"));

        if(reservation.getPayments()
                .stream()
                .mapToDouble(payment -> payment.getAmount()).sum() + request.getAmount() > reservation.getTotalAmount()) {
            throw new RuntimeException("Payment exceeds total price");
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
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return new PaymentResponse(payment);
    }

    public PaymentResponse completePayment(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(savedPayment);
    }
}
