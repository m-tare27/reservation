package com.reservation.aspect;

import com.reservation.dto.CancellationResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CancellationAspect {

    @Pointcut("execution(* com.reservation.service.CancellationService.cancelReservation(..))")
    public void cancelReservationPointcut() {}

    @AfterReturning(pointcut = "cancelReservationPointcut()", returning = "response")
    public void logCancelReservation(CancellationResponse response) {
        log.info("Reservation cancellation processed successfully: {}", response);
    }
}
