package com.reservation.listener;

import com.reservation.dto.event.ReservationConfirmedEvent;
import com.reservation.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LoyaltyListener {

    private final GuestService guestService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleLoyaltyPointsUpdate(
            ReservationConfirmedEvent event
    ) {
        guestService.addLoyaltyPoints(
                event.getGuestId(),
                event.getTotalAmount()
        );
    }
}
