package com.reservation.service;

import com.reservation.entity.Reservation;
import com.reservation.enums.ReservationStatus;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitlistPromotionService {

    private final ReservationRepository reservationRepository;

//    @Transactional
//    public void promoteWaitlist(Integer bungalowId) {
//
//        List<Reservation> waitlistedReservations =
//                reservationRepository
//                        .findByBungalow_IdAndReservationStatusOrderByCreatedAtAsc(
//                                bungalowId,
//                                ReservationStatus.WAITLIST
//                        );
//
//        for (Reservation reservation : waitlistedReservations) {
//
////            boolean exists =
////                    reservationRepository.existsOverlappingReservation(
////                            reservation.getId(),
////                            reservation.getBungalowId(),
////                            reservation.getArrivalDate(),
////                            reservation.getDepartureDate()
////                    );
////
////            if (!exists) {
////
////                reservation.setReservationStatus(
////                        ReservationStatus.PENDING
////                );
////
////                reservationRepository.save(reservation);
////                break;
////            }
//        }
//    }
}
