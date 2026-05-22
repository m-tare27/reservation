package com.reservation.listener;

import com.reservation.dto.event.ReservationCancelledEvent;
import com.reservation.dto.event.ReservationConfirmedEvent;
import com.reservation.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleReservationConfirmedEmail(
            ReservationConfirmedEvent event
    ) {

        System.out.println("Sending email to: " + event.getEmail());

        emailService.sendReservationEmail(
                event.getEmail(),
                event.getReservationId()
        );

        System.out.println("Email successfully sent to: " + event.getEmail());
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleReservationCancelledEmail(
            ReservationCancelledEvent event
    ) {

        System.out.println("Sending email to: " + event.getEmail());

        emailService.sendReservationCancellationEmail(
                event.getEmail(),
                event.getReservationId()
        );

        System.out.println("Email successfully sent to: " + event.getEmail());
    }
}
