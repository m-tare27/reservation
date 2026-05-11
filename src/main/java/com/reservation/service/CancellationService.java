package com.reservation.service;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.dto.event.ReservationCancelledEvent;
import com.reservation.entity.*;
import com.reservation.enums.PaymentStatus;
import com.reservation.enums.RefundStatus;
import com.reservation.enums.ReservationStatus;
import com.reservation.repository.CancellationPolicyRepository;
import com.reservation.repository.CancellationRepository;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final ApplicationEventPublisher eventPublisher;

    public CancellationResponse cancelReservation(CancellationRequest request){
        Reservation reservation = reservationRepository.findById(request.getId())
                .orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Invalid reservation id"
                ));

        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            Cancellation existing = cancellationRepository.findByReservation(reservation)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Reservation is already cancelled"
                    ));
            return new CancellationResponse(existing);
        }

        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED &&
                reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only CONFIRMED or PENDING reservations can be cancelled"
            );
        }

        long daysBeforeCheckIn = ChronoUnit.DAYS.between(LocalDate.now() , reservation.getArrivalDate());

        if (daysBeforeCheckIn < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot cancel after check-in date"
            );
        }

        CancellationPolicy policy = cancellationPolicyRepository.findApplicablePolicy(daysBeforeCheckIn)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No applicable cancellation policy found for " + daysBeforeCheckIn + " days before check-in"
                ));

        Cancellation cancellation = new Cancellation();
        cancellation.setReservation(reservation);
        cancellation.setCancellationPolicy(policy);
        cancellation.setDaysBeforeCheckIn(daysBeforeCheckIn);
        cancellation.setCancelledAt(LocalDateTime.now());
        cancellation.setRefundStatus(RefundStatus.PENDING);
        cancellation.setReason(request.getReason());

        BigDecimal totalAmount = BigDecimal.valueOf(reservation.getTotalAmount());
        BigDecimal refundPercentage = BigDecimal.valueOf(policy.getRefundPercentage());
        BigDecimal refundAmount = totalAmount
                .multiply(refundPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        cancellation.setRefundAmount(refundAmount.doubleValue());
        reservation.setReservationStatus(ReservationStatus.CANCELLED);

        Cancellation savedCancellation = cancellationRepository.save(cancellation);

        if (reservation.getCommission() != null){
            Commission commission = reservation.getCommission();
            if (commission.getPayments() != null) {
                commission.getPayments()
                        .forEach(payment ->
                                payment.setPaymentStatus(PaymentStatus.CANCELLED));
            }
        }

        eventPublisher.publishEvent(
                new ReservationCancelledEvent(
                        reservation.getGuest().getEmail(),
                        reservation.getGuest().getName(),
                        reservation.getId(),
                        reservation.getBungalowId()
                )
        );

        return new CancellationResponse(savedCancellation);
    }

    public CancellationResponse getCancellationById(Integer id){
        Cancellation cancellation =  cancellationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cancellation with id " + id + " not found"
                ));
        return new CancellationResponse(cancellation);
    }

    public List<CancellationResponse> getCancellations(
            LocalDateTime cancelledAt,
            Long daysBeforeCheckIn,
            RefundStatus refundStatus) {

        if (cancelledAt != null) {
            return convertToResponseList(
                    cancellationRepository.findByCancelledAt(cancelledAt));
        }

        if (daysBeforeCheckIn != null) {
            return convertToResponseList(
                    cancellationRepository.findByDaysBeforeCheckInEquals(daysBeforeCheckIn));
        }

        if (refundStatus != null) {
            return convertToResponseList(
                    cancellationRepository.findByRefundStatus(refundStatus));
        }
        return convertToResponseList(cancellationRepository.findAll());
    }

    //Helper Methods
    private List<CancellationResponse> convertToResponseList(List<Cancellation> cancellations) {
        return cancellations.stream()
                .map(CancellationResponse::new)
                .toList();
    }

    public CancellationResponse updateRefundStatus(Integer id, RefundStatus refundStatus) {
        Cancellation cancellation = cancellationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cancellation with id " + id + " not found"
                ));
        cancellation.setRefundStatus(refundStatus);
        Cancellation updatedCancellation = cancellationRepository.save(cancellation);
        return new CancellationResponse(updatedCancellation);
    }
}
