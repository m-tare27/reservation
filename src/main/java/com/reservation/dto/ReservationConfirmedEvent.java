package com.reservation.dto;

import com.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationConfirmedEvent {
    private String email;
    private String guestName;
    private Integer reservationId;
}
