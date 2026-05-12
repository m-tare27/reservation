package com.reservation.processor;

import com.reservation.entity.Reservation;
import com.reservation.enums.ReservationStatus;
import com.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@RequiredArgsConstructor
@Slf4j
public class ReservationItemProcessor
        implements ItemProcessor<Reservation, Reservation> {

    @Override
    public Reservation process(Reservation item) {

        if (item.getReservationStatus() != ReservationStatus.PENDING) {
            return null;
        }

        item.setReservationStatus(ReservationStatus.EXPIRED);

        log.info("Marked Reservation {} as EXPIRED", item.getId());

        return item;
    }
}
