package com.reservation.service;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.entity.*;
import com.reservation.repository.CancellationPolicyRepository;
import com.reservation.repository.CancellationRepository;
import com.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CancellationService {
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final ReservationRepository reservationRepository;
    private final CancellationRepository cancellationRepository;


    public CancellationResponse cancelReservation(CancellationRequest request){
        Reservation reservation = reservationRepository.findById(request.getId())
                .orElseThrow(()-> new RuntimeException("Invalid Reservation"));

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        Cancellation cancellation = new Cancellation();

        long days = ChronoUnit.DAYS.between(LocalDate.now() , reservation.getArrivalDate());

        CancellationPolicy cancellationPolicy = cancellationPolicyRepository.findAll()
                        .stream()
                                .filter(x -> x.getDaysBeforeCheckInFrom() <= days && days <= x.getDaysBeforeCheckInTo())
                                .findFirst()
                                        .orElseThrow();

        cancellation.setReservation(reservation);
        cancellation.setCancellationPolicy(cancellationPolicy);
        cancellation.setDaysBeforeCheckIn(days);
        cancellation.setCancelledAt(LocalDateTime.now());
        cancellation.setRefundStatus(RefundStatus.PENDING);

        double paid = reservation.getTotalAmount();
        double refund = paid * cancellationPolicy.getRefundPercentage()/100;
        cancellation.setRefundAmount(refund);
        cancellation.setReason(request.getReason());

        reservationRepository.save(reservation);
        Cancellation savedCancellation = cancellationRepository.save(cancellation);
        return new CancellationResponse(savedCancellation);
    }
}
