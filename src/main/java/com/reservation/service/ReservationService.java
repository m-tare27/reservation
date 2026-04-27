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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with email " + request.getGuestEmail() + " not found"));

        if (isInvalidReservationDate(request.getArrivalDate(), request.getDepartureDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival date must be today or later, and departure must be after arrival");
        }

        Reservation reservation = new Reservation();
        Mapper.mapRequestToEntity(reservation , request , guest);
        reservation.setCreatedAt(LocalDateTime.now());

        boolean exists = reservationRepository.existsOverlappingReservation(null , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        reservation.setReservationStatus(exists ? ReservationStatus.WAITLIST : ReservationStatus.PENDING);
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse updateReservation(ReservationRequest request , Integer id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + id + " not found"));

        if (isInvalidReservationDate(request.getArrivalDate(), request.getDepartureDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date range provided");
        }

        boolean exists = reservationRepository.existsOverlappingReservation(id , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        if(exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bungalow is booked for those dates"
            );

        Mapper.mapRequestToEntity(reservation , request , reservation.getGuest());
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public void updateReservationStatus(Integer id, ReservationStatus status){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"));

        ReservationStatus currentStatus = reservation.getReservationStatus();

        if (currentStatus != ReservationStatus.PENDING && currentStatus != ReservationStatus.WAITLIST) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PENDING or WAITLIST reservations can be updated");
        }

        if (currentStatus == ReservationStatus.WAITLIST && status == ReservationStatus.CONFIRMED) {
            boolean hasConfirmedReservation = reservationRepository.existsConfirmedReservationForSameBungalow(id);
            if (hasConfirmedReservation) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot confirm: another confirmed reservation exists for this bungalow");
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

    public ReservationResponse getReservationById(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + id + " not found"));
        return new ReservationResponse(reservation);
    }

    public List<ReservationResponse> getReservations(
            Integer id,
            Integer bungalowId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate) {

        if (id != null) {
            return List.of(getReservationById(id));
        }

        if (bungalowId != null) {
            return convertToResponseList(reservationRepository.findByBungalowId(bungalowId));
        }

        if (status != null) {
            return convertToResponseList(reservationRepository.findByReservationStatus(status));
        }

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Start date must be before end date");
            }
            return convertToResponseList(
                    reservationRepository.findByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
                            endDate, startDate));
        }
        return convertToResponseList(reservationRepository.findAll());
    }
        //Helper methods

    private List<ReservationResponse> convertToResponseList(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public boolean isInvalidReservationDate(LocalDate arrivalDate, LocalDate departureDate) {
        return arrivalDate.isBefore(LocalDate.now()) ||
                !departureDate.isAfter(arrivalDate);
    }
}
