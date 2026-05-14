package com.reservation.listener;

import com.reservation.dto.event.ReservationCancelledEvent;
import com.reservation.service.ReservationService;
import com.reservation.service.WaitlistPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCancelledListener {

    private final WaitlistPromotionService waitlistPromotionService;
    @EventListener
    public void handleReservationCancelledEvent(ReservationCancelledEvent event) {
        waitlistPromotionService.promoteWaitlist(event.getBungalowId());
    }
}
