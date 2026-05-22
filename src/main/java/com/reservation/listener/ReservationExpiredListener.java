package com.reservation.listener;

import com.reservation.dto.event.ReservationExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiredListener {

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleReservationExpiredEvent(ReservationExpiredEvent event) {
        log.info(
                "Received reservation expired event for reservation {}",
                event.getReservationId()
        );
    }
}
