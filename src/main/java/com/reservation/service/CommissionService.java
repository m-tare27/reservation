package com.reservation.service;

import com.reservation.entity.Commission;
import com.reservation.entity.Payment;
import com.reservation.entity.Reservation;
import com.reservation.entity.TravelAgent;
import com.reservation.enums.PaymentStatus;
import com.reservation.enums.ReservationStatus;
import com.reservation.repository.CommissionRepository;
import com.reservation.repository.PaymentRepository;
import com.reservation.repository.ReservationRepository;
import com.reservation.repository.TravelAgentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final ReservationRepository reservationRepository;
    private final TravelAgentRepository travelAgentRepository;
    private final PaymentRepository paymentRepository;

        public void createCommissionForReservation(Integer reservationId , Integer travelAgentId) {

            if (commissionRepository.existsByReservationId(reservationId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Commission already exists for reservation ID: " + reservationId
                );
            }

            TravelAgent travelAgent = travelAgentRepository.findById(travelAgentId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Travel Agent not found with ID: " + travelAgentId
                    ));

            Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Reservation not found with ID: " + reservationId
                    ));

            BigDecimal commissionAmount = reservation.getTotalAmount()
                            .multiply(BigDecimal.valueOf(travelAgent.getCommissionRate()))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            Commission commission = new Commission();
            commission.setReservation(reservation);
            commission.setTravelAgent(travelAgent);
            commission.setAmount(commissionAmount);

            Commission savedCommission = commissionRepository.save(commission);
            Payment payment = new Payment();

            payment.setCommission(savedCommission);
            payment.setAmount(savedCommission.getAmount());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);
            
            reservation.setCommission(savedCommission);
        }

        public Commission getCommissionByReservationId(Integer reservationId) {
            return commissionRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Commission not found for reservation ID: " + reservationId
                    ));
        }

        public void markCommissionPaymentsAsCompleted(Integer commissionId) {
            Commission commission = commissionRepository.findByIdForUpdate(commissionId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Commission not found with ID: " + commissionId
                    ));

            if (commission.getPayments() != null) {
                commission.getPayments().forEach(payment -> {
                    validatePaymentTransition(payment.getPaymentStatus(), PaymentStatus.COMPLETED);
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                });
            }

            commissionRepository.save(commission);
        }

    private void validatePaymentTransition(
            PaymentStatus currentStatus,
            PaymentStatus newStatus
    ) {

        if (currentStatus == newStatus) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already in status " + newStatus
            );
        }

        switch (currentStatus) {

            case PENDING -> {
                if (newStatus != PaymentStatus.COMPLETED &&
                        newStatus != PaymentStatus.CANCELLED) {

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
