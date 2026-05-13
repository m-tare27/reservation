package com.reservation.dto.event;

import com.reservation.enums.BookingSource;
import com.reservation.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreationEvent {

    private Integer reservationId;
    private Integer travelAgencyId;
    private BookingSource bookingSource;
}
