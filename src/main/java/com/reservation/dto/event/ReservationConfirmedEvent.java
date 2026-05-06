package com.reservation.dto.event;

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
