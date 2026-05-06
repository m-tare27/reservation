package com.reservation.aspect;

import com.reservation.dto.ReservationResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ReservationAspect {

    @Pointcut("execution(* com.reservation.service.ReservationService.createReservation(..))")
    public void createReservationPointcut() {}

    @AfterReturning(pointcut = "createReservationPointcut()", returning = "response")
    public void logCreateReservation(ReservationResponse response) {
        log.info("Reservation created with ID: {}", response.getReservationId());
    }
}
