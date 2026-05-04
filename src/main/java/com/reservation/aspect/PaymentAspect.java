package com.reservation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class PaymentAspect {

    @Pointcut("execution(* com.reservation.service.PaymentService.getRevenueByBungalowId(..))")
    public void getRevenueByBungalowIdPointcut() {}

    @Pointcut("execution(* com.reservation.service.PaymentService.processPayment(..))")
    public void processPaymentPointcut() {}

    @AfterReturning(pointcut = "getRevenueByBungalowIdPointcut()", returning = "revenue")
    public void logRevenueByBungalowId(Double revenue) {
        log.info("Calculating revenue for bungalow...");
        log.info("Revenue for bungalow: {}", revenue);
    }

    @Before("processPaymentPointcut()")
    public void logProcessPayment() {
        log.info("Processing payment...");
    }

    @AfterReturning(pointcut = "processPaymentPointcut()", returning = "response")
    public void logProcessPaymentResponse(Object response) {
        log.info("Payment processed successfully: {}", response);
    }

    @AfterThrowing(pointcut = "processPaymentPointcut()", throwing = "ex")
    public void logProcessPaymentException(Exception ex) {
        log.error("Error processing payment: {}", ex.getMessage());
    }

}