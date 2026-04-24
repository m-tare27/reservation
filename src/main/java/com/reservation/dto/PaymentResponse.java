package com.reservation.dto;

import com.reservation.entity.Payment;
import lombok.Data;

@Data
public class PaymentResponse {
    private Integer paymentId;
    private Integer reservationId;
    private Double amount;

    public PaymentResponse(Payment payment) {
        this.paymentId = payment.getId();
        this.reservationId = payment.getReservation().getId();
        this.amount = payment.getAmount();
    }
}
