package com.reservation.listener;

import com.reservation.dto.event.ReservationExpiredEvent;
import com.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationWriteListener implements ItemWriteListener<Reservation> {

    private final ApplicationEventPublisher publisher;

    @Override
    public void afterWrite(Chunk<? extends Reservation> items) {
        // your logic
        for (Reservation r : items) {
            publisher.publishEvent(
                    new ReservationExpiredEvent(r.getId(), r.getBungalow().getId())
            );
        }
    }

}