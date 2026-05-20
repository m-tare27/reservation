package com.reservation.listener;

import com.reservation.dto.event.ReservationCancelledEvent;
import com.reservation.service.PaymentService;
import com.reservation.service.ReservationService;
import com.reservation.service.WaitlistPromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCancelledListener {

    private final WaitlistPromotionService waitlistPromotionService;
    private final PaymentService paymentService;

    @EventListener
    @Transactional
    public void handleReservationCancelledEvent(ReservationCancelledEvent event) {
        paymentService.cancelPayments(event.getReservationId());
        paymentService.refundPayment(event.getReservationId());
        waitlistPromotionService.promoteWaitlist(event.getBungalowId());
    }
}
