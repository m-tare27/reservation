package com.reservation.service;

import com.reservation.entity.Commission;
import com.reservation.entity.Reservation;
import com.reservation.entity.TravelAgent;
import com.reservation.repository.CommissionRepository;
import com.reservation.repository.ReservationRepository;
import com.reservation.repository.TravelAgentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final ReservationRepository reservationRepository;
    private final TravelAgentRepository travelAgentRepository;

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

            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Reservation not found with ID: " + reservationId
                    ));

            Double commissionAmount = reservation.getTotalAmount() * travelAgent.getCommissionRate()/100;

            Commission commission = new Commission();
            commission.setReservation(reservation);
            commission.setTravelAgent(travelAgent);
            commission.setAmount(commissionAmount);

            commissionRepository.save(commission);
            reservation.setCommission(commission);
        }
}
