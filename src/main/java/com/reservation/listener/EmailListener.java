package com.reservation.listener;

import com.reservation.config.RabbitConfig;
import com.reservation.dto.event.ReservationCancelledEvent;
import com.reservation.dto.event.ReservationConfirmedEvent;
import com.reservation.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handleReservationEmail(ReservationConfirmedEvent event) {

        System.out.println("Sending email to: " + event.getEmail());

        emailService.sendReservationEmail(
                event.getEmail(),
                event.getReservationId()
        );

        System.out.println("Email successfully sent to: " + event.getEmail());
    }

    @EventListener
    public void handleReservationCancelledEmail(ReservationCancelledEvent event){

        System.out.println("Sending email to: " + event.getEmail());

        emailService.sendReservationCancellationEmail(
                event.getEmail(),
                event.getReservationId()
        );

        System.out.println("Email successfully sent to: " + event.getEmail());
    }
}
