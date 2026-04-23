package com.reservation.processor;

import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationItemProcessor implements ItemProcessor<Reservation , Reservation> {
    private static final Logger log = LoggerFactory.getLogger(ReservationItemProcessor.class);
    @Override
    public @Nullable Reservation process(Reservation item) throws Exception {
        item.setReservationStatus(ReservationStatus.EXPIRED);
        log.info("Moved Reservation {} to EXPIRED",item.getId());
        return item;
    }
}
