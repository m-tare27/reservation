package com.reservation.service;

import com.reservation.entity.*;
import com.reservation.enums.PayoutStatus;
import com.reservation.enums.ReservationStatus;
import com.reservation.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final ReservationRepository reservationRepository;
    private final CommissionPayoutRepository payoutRepository;

    public void createCommissionForReservation(Integer reservationId) {

        if (commissionRepository.existsByReservationId(reservationId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Commission already exists for reservation ID: " + reservationId
            );
        }

        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found with ID: " + reservationId
                ));

        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Commission can only be created for CONFIRMED reservations"
            );
        }

        TravelAgent travelAgent = reservation.getTravelAgent();
        if (travelAgent == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reservation has no associated travel agent"
            );
        }

        BigDecimal commissionAmount = reservation.getTotalAmount()
                .multiply(BigDecimal.valueOf(travelAgent.getCommissionRate()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        Commission commission = new Commission();
        commission.setReservation(reservation);
        commission.setTravelAgent(travelAgent);
        commission.setAmount(commissionAmount);

        Commission savedCommission = commissionRepository.save(commission);

        CommissionPayout payout = new CommissionPayout();
        payout.setCommission(savedCommission);
        payout.setAmount(savedCommission.getAmount());
        payout.setPayoutStatus(PayoutStatus.PENDING);

        payoutRepository.save(payout);

        reservation.setCommission(savedCommission);
    }

    public void completeCommissionPayout(Integer commissionId) {

        Commission commission = commissionRepository.findByIdForUpdate(commissionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Commission not found with ID: " + commissionId
                ));

        CommissionPayout payout = commission.getPayout();

        if (payout == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No payout exists for commission"
            );
        }

        validatePayoutTransition(
                payout.getPayoutStatus(),
                PayoutStatus.COMPLETED
        );

        payout.setPayoutStatus(PayoutStatus.COMPLETED);
        payout.setPayoutDate(LocalDate.now());

        payoutRepository.save(payout);
    }

    private void validatePayoutTransition(
            PayoutStatus currentStatus,
            PayoutStatus newStatus
    ) {

        if (currentStatus == newStatus) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already in status " + newStatus
            );
        }

        switch (currentStatus) {

            case PENDING -> {
                if (newStatus != PayoutStatus.COMPLETED &&
                        newStatus != PayoutStatus.CANCELLED) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Pending payments can only move to COMPLETED or CANCELLED"
                    );
                }
            }

            case CANCELLED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancelled payments cannot change state"
            );

            case COMPLETED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed payments cannot change state"
            );
        }
    }
}
