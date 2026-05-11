package com.reservation.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationExpiredEvent {

    Integer reservationId;
    Integer bungalowId;
}
