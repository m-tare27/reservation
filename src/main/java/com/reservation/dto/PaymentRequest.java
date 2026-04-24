package com.reservation.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer reservationId;
    private Double amount;
}
