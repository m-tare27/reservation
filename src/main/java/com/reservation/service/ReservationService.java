package com.reservation.service;

import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.entity.Guest;
import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import com.reservation.mapper.Mapper;
import com.reservation.repository.GuestRepository;
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
    private final GuestRepository guestRepository;
    private final EmailService emailService;

    public ReservationResponse createReservation(ReservationRequest request){
        Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                .orElseThrow(()-> new RuntimeException("Invalid Guest Email"));

        Reservation reservation = new Reservation();

        if (isInvalidReservationDate(request.getArrivalDate() , request.getDepartureDate()))
            throw new RuntimeException("Invalid date input");

        Mapper.mapRequestToEntity(reservation , request , guest);
        reservation.setCreatedAt(LocalDateTime.now());

        boolean exists = reservationRepository.existsOverlappingReservation(null , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        if(exists){
            reservation.setReservationStatus(ReservationStatus.WAITLIST);
        }
        else {
            reservation.setReservationStatus(ReservationStatus.PENDING);
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse updateReservation(ReservationRequest request , Integer id){
        Reservation reservation = reservationRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        Guest guest = reservation.getGuest();

        if (isInvalidReservationDate(request.getArrivalDate() , request.getDepartureDate()))
            throw new RuntimeException("Invalid date input");

        boolean exists = reservationRepository.existsOverlappingReservation(id , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        if(exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bungalow is booked for those dates"
            );

        Mapper.mapRequestToEntity(reservation , request , guest);

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public void updateReservationStatus(Integer id, ReservationStatus status){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Invalid Reservation Id"));

        if (reservation.getReservationStatus() != ReservationStatus.PENDING && reservation.getReservationStatus() != ReservationStatus.WAITLIST) {
            throw new RuntimeException("Only pending or waitlist reservations can be updated");
        }

        if (reservation.getReservationStatus() == ReservationStatus.WAITLIST && status == ReservationStatus.CONFIRMED) {
            boolean exists = reservationRepository.existsConfirmedReservationForSameBungalow(id);
            if (exists) {
                throw new RuntimeException("Cannot confirm waitlist reservation because there is already a confirmed reservation for the same bungalow");
            }
        }

        reservation.setReservationStatus(status);
        reservationRepository.save(reservation);

        if (status == ReservationStatus.CONFIRMED){
            emailService.sendReservationEmail(
                    reservation.getGuest().getEmail() ,reservation
            );
        }
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
