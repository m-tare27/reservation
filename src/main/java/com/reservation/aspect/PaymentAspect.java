package com.reservation.aspect;

import com.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentAspect {

    private final ReservationRepository reservationRepository;

    @After("execution(* com.reservation.service.PaymentService.completePayment(..))")
    public void afterPaymentProcessing() {
        log.info("Payment completed successfully. Sending confirmation email to guest...");

    }
}
