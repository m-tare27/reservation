package com.reservation.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class ReservationConfirmedEvent {
    private String email;
    private String guestName;
    private Integer reservationId;

    private Integer guestId;
    private BigDecimal totalAmount;

    public ReservationConfirmedEvent(String email, String guestName, Integer reservationId , Integer guestId, BigDecimal totalAmount) {
        this.email = email;
        this.guestName = guestName;
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.totalAmount = totalAmount;
    }
}
