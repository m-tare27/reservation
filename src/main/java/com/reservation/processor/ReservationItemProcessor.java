package com.reservation.processor;

import com.reservation.entity.Reservation;
import com.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@RequiredArgsConstructor
public class ReservationItemProcessor
        implements ItemProcessor<Reservation, Reservation> {

    private final ReservationService reservationService;

    @Override
    public Reservation process(Reservation item) {

        reservationService.expireReservation(item);

        return null;
    }
}
