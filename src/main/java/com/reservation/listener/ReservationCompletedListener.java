package com.reservation.listener;

import com.reservation.dto.event.ReservationCompletedEvent;
import com.reservation.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationCompletedListener {

    private final CommissionService commissionService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleReservationCompletedEvent(
            ReservationCompletedEvent event
    ) {

        if (event.getCommissionId() != null) {
            commissionService.completeCommissionPayout(
                    event.getCommissionId()
            );
        }
    }
}
