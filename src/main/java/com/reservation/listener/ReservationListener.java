package com.reservation.listener;

import com.reservation.dto.event.ReservationCreationEvent;
import com.reservation.service.CommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {
    private final CommissionService commissionService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleReservationCreationEvent(ReservationCreationEvent event) {
        log.info("Received reservation event for reservation ID: {}", event.getReservationId());

        if (event.getTravelAgencyId() != null) {
            log.info("Processing commission for travel agency ID: {}", event.getTravelAgencyId());
            try {
                commissionService.createCommissionForReservation(
                        event.getReservationId()
                );
            } catch (Exception ex) {
                log.error("Failed to create commission", ex);
            }
        } else {
            log.debug(
                    "Skipping commission creation for reservation {}",
                    event.getReservationId()
            );
        }
    }
}
