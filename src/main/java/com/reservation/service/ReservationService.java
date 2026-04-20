package com.reservation.service;

import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import com.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationResponse createReservation(ReservationRequest request){
        Reservation reservation = new Reservation();

        reservation.setBungalowId(request.getBungalowId());
        reservation.setGuestName(request.getGuestName());
        reservation.setGuestEmail(request.getGuestEmail());
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        reservation.setTotalAmount(request.getTotalAmount());
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse updateReservation(ReservationRequest request , Integer id){
        Reservation reservation = reservationRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        reservation.setBungalowId(request.getBungalowId());
        reservation.setGuestName(request.getGuestName());
        reservation.setGuestEmail(request.getGuestEmail());
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        reservation.setTotalAmount(request.getTotalAmount());

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public void updateReservationStatus(Integer id, ReservationStatus status){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        reservation.setReservationStatus(status);
        reservationRepository.save(reservation);
    }

    public ReservationResponse getReservationById(Integer id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        return new ReservationResponse(reservation);
    }

    public List<ReservationResponse> getReservationByBungalowId(Integer id){
        return reservationRepository.findByBungalowId(id)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<ReservationResponse> getReservationByStatus(ReservationStatus status){
        return reservationRepository.findByReservationStatus(status)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

}
