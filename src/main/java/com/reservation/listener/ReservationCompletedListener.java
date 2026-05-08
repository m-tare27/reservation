package com.reservation.listener;

import com.reservation.config.RabbitConfig;
import com.reservation.dto.event.ReservationCompletedEvent;
import com.reservation.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCompletedListener {

    private final CommissionService commissionService;

    @RabbitListener(queues = RabbitConfig.RESERVATION_COMPLETED_QUEUE)
    public void handleReservationCompletedEvent(ReservationCompletedEvent event) {
        if (event.getCommissionId() != null) {
            commissionService.markCommissionPaymentsAsCompleted(event.getCommissionId());
        }
    }
}
