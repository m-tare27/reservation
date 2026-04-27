package com.reservation.aspect;

import com.reservation.entity.Payment;
import com.reservation.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentAspect {

    private final PaymentRepository paymentRepository;

    @After("execution(* com.reservation.service.PaymentService.processPayment(..))")
    public void logPaymentProcessing() {
        log.info("Payment processing completed.");
    }

    @After("execution(* com.reservation.service.PaymentService.completePayment(..))")
    public void logPaymentCompletion(JoinPoint joinPoint) {
        log.info("Payment completion completed.");

        Object[] args = joinPoint.getArgs();
        Integer paymentId = (Integer) args[0];

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Integer reservationId = payment.getReservation().getId();
        Double totalAmount = payment.getReservation().getTotalAmount();

        Double amount = paymentRepository.sumCompletedPaymentsByReservationId(reservationId); // Example reservation ID
        log.info("Total completed payment amount for reservation ID {}: {}", reservationId ,amount);

        if (amount >= totalAmount) {
            log.info("Payment for reservation ID {} is complete. Total amount paid: {}", reservationId, amount);
        } else {
            log.info("Payment for reservation ID {} is incomplete. Total amount paid: {}, Remaining amount: {}", reservationId, amount, totalAmount - amount);
        }

    }
}
