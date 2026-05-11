package com.reservation.listener;

import com.reservation.dto.event.ReservationExpiredEvent;
import com.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiredListener {

    @EventListener
    public void handleReservationExpiredEvent(ReservationExpiredEvent event) {
        log.info(
                "Received reservation expired event for reservation {}",
                event.getReservationId()
        );


    }
}
