package com.reservation.listener;

import com.reservation.config.RabbitConfig;
import com.reservation.dto.event.ReservationConfirmedEvent;
import com.reservation.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoyaltyListener {

    private final GuestService guestService;

    @RabbitListener(queues = RabbitConfig.LOYALTY_QUEUE)
    public void handleLoyaltyPointsUpdate(ReservationConfirmedEvent event) {
        guestService.addLoyaltyPoints(event.getGuestId() , event.getTotalAmount());
    }
}
