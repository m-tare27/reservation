package com.reservation.dto;

import com.reservation.entity.Payment;
import com.reservation.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResponse {
    private Integer paymentId;
    private ReservationResponse reservation;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;

    public PaymentResponse(Payment payment) {
        this.paymentId = payment.getId();
        this.reservation = new ReservationResponse(payment.getReservation());
        this.amount = payment.getAmount();
        this.paymentStatus = payment.getPaymentStatus();
    }
}
