package com.reservation.service;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.entity.*;
import com.reservation.repository.CancellationPolicyRepository;
import com.reservation.repository.CancellationRepository;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CancellationService {
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final ReservationRepository reservationRepository;
    private final CancellationRepository cancellationRepository;


    public CancellationResponse cancelReservation(CancellationRequest request){
        Reservation reservation = reservationRepository.findById(request.getId())
                .orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid reservation id"
                ));

        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            Cancellation existing = cancellationRepository.findByReservation(reservation)
                    .orElseThrow(()-> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Cancellation record missing"
                    ));
            return new CancellationResponse(existing);
        }


        long days = ChronoUnit.DAYS.between(LocalDate.now() , reservation.getArrivalDate());

        if (days < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot cancel after check-in date"
            );
        }

        CancellationPolicy cancellationPolicy = cancellationPolicyRepository.findApplicablePolicy(days)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "No policy applicable"
                        ));

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        Cancellation cancellation = new Cancellation();

        cancellation.setReservation(reservation);
        cancellation.setCancellationPolicy(cancellationPolicy);
        cancellation.setDaysBeforeCheckIn(days);
        cancellation.setCancelledAt(LocalDateTime.now());
        cancellation.setRefundStatus(RefundStatus.PENDING);


        double refund = reservation.getTotalAmount() * cancellationPolicy.getRefundPercentage()/100;
        cancellation.setRefundAmount(refund);
        cancellation.setReason(request.getReason());


        Cancellation savedCancellation = cancellationRepository.save(cancellation);
        return new CancellationResponse(savedCancellation);
    }

    public CancellationResponse getCancellationById(Integer id){
        Cancellation cancellation =  cancellationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No cancellations for this id"
                ));
        return new CancellationResponse(cancellation);
    }

    public List<CancellationResponse> getAllCancellation(){
        return cancellationRepository.findAll()
                .stream()
                .map(CancellationResponse::new)
                .toList();
    }

    public List<CancellationResponse> getAllCancellationByCancelledAt(LocalDateTime time){
        return cancellationRepository.findByCancelledAt(time)
                .stream()
                .map(CancellationResponse::new)
                .toList();
    }

    public List<CancellationResponse> getAllCancellationByDaysBeforeCheckIn(long days){
        return cancellationRepository.findByDaysBeforeCheckIn(days)
                .stream()
                .map(CancellationResponse::new)
                .toList();
    }

    public List<CancellationResponse> getAllCancellationByRefundStatus(RefundStatus status){
        return cancellationRepository.findByRefundStatus(status)
                .stream()
                .map(CancellationResponse::new)
                .toList();
    }
}
