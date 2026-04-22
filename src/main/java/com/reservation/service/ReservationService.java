package com.reservation.service;

import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import com.reservation.mapper.Mapper;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationResponse createReservation(ReservationRequest request){
        Reservation reservation = new Reservation();

        if (isInvalidReservationDate(request.getArrivalDate() , request.getDepartureDate()))
            throw new RuntimeException("Invalid date input");

        boolean exists = reservationRepository.existsOverlappingReservation(null , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        if(exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bungalow is booked for those dates"
            );

        Mapper.mapRequestToEntity(reservation , request);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse updateReservation(ReservationRequest request , Integer id){
        Reservation reservation = reservationRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        if (isInvalidReservationDate(request.getArrivalDate() , request.getDepartureDate()))
            throw new RuntimeException("Invalid date input");

        boolean exists = reservationRepository.existsOverlappingReservation(id , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        if(exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bungalow is booked for those dates"
            );

        Mapper.mapRequestToEntity(reservation , request);

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public void updateReservationStatus(Integer id, ReservationStatus status){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Only pending reservations can be updated");
        }

        reservation.setReservationStatus(status);
        reservationRepository.save(reservation);
    }

    public List<ReservationResponse> getReservation() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::new)
                .toList();
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

    public List<ReservationResponse> getReservationByDateRange(LocalDate startDate , LocalDate endDate){
        return reservationRepository.findByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(endDate , startDate)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public boolean isInvalidReservationDate(LocalDate arrivalDate, LocalDate departureDate) {
        return arrivalDate.isBefore(LocalDate.now()) ||
                !departureDate.isAfter(arrivalDate);
    }

}
