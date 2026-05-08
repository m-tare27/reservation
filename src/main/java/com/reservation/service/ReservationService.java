package com.reservation.service;

import com.reservation.config.RabbitConfig;
import com.reservation.dto.event.ReservationCompletedEvent;
import com.reservation.dto.event.ReservationConfirmedEvent;
import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.dto.event.ReservationCreationEvent;
import com.reservation.entity.*;
import com.reservation.enums.BookingSource;
import com.reservation.enums.ReservationStatus;
import com.reservation.mapper.Mapper;
import com.reservation.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RabbitTemplate rabbitTemplate;


    public ReservationResponse createReservation(ReservationRequest request){
        validateReservation(request);
        Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with email " + request.getGuestEmail() + " not found"));

        Reservation reservation = new Reservation();
        Mapper.mapRequestToEntity(reservation , request , guest);

        boolean exists = reservationRepository.existsOverlappingReservation(null , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        reservation.setReservationStatus(exists ? ReservationStatus.WAITLIST : ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        ReservationCreationEvent reservationCreationEvent = new ReservationCreationEvent(
                savedReservation.getId(),
                request.getTravelAgentId(),
                request.getBookingSource()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.RESERVATION_CREATED_EXCHANGE,
                "",
                reservationCreationEvent
        );

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse updateReservation(ReservationRequest request , Integer id){
        Reservation reservation = reservationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + id + " not found"));


        validateReservation(reservation , request);

        Mapper.mapRequestToEntity(reservation , request , reservation.getGuest());
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public void updateReservationStatus(Integer id, ReservationStatus status){
        Reservation reservation = reservationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"));

        validateReservationUpdate(reservation , status);

        reservation.setReservationStatus(status);
        reservationRepository.save(reservation);

        ReservationConfirmedEvent reservationConfirmedEvent =
                new ReservationConfirmedEvent(
                        reservation.getGuest().getEmail(),
                        reservation.getGuest().getName(),
                        reservation.getId(),
                        reservation.getGuest().getId(),
                        reservation.getTotalAmount()
                );

        rabbitTemplate.convertAndSend(
                RabbitConfig.RESERVATION_CONFIRMED_EXCHANGE,
                "",
                reservationConfirmedEvent
        );
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

    public ReservationResponse completeReservation(Integer id) {
        Reservation reservation = reservationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"));

        reservation.setReservationStatus(ReservationStatus.COMPLETED);
        Reservation savedReservation = reservationRepository.save(reservation);

        ReservationCompletedEvent reservationCompletedEvent = new ReservationCompletedEvent(
                reservation.getId(),
                null
        );

        if (reservation.getBookingSource() == BookingSource.TRAVEL_AGENCY) {
            reservationCompletedEvent.setCommissionId(reservation.getCommission().getId());
        }
        rabbitTemplate.convertAndSend(
                RabbitConfig.RESERVATION_COMPLETED_EXCHANGE,
                "",
                reservationCompletedEvent
        );

        return new ReservationResponse(savedReservation);
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

    public void validateReservation(ReservationRequest request) {
        if (BookingSource.TRAVEL_AGENCY.equals(request.getBookingSource())) {

            if (request.getTravelAgentId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Travel agent ID is required when booking source is TRAVEL_AGENCY");
            }
        }
        if (isInvalidReservationDate(request.getArrivalDate(), request.getDepartureDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival date must be today or later, and departure must be after arrival");
        }
    }

    public void validateReservation(Reservation reservation , ReservationRequest request) {
        if (isInvalidReservationDate(request.getArrivalDate(), request.getDepartureDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date range provided");
        }

        boolean exists = reservationRepository.existsOverlappingReservation(reservation.getId() , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        if(exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bungalow is booked for those dates"
            );

        if (reservation.getReservationStatus() == ReservationStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Confirmed reservations cannot be updated");
        }
    }

    public void validateReservationUpdate(Reservation reservation , ReservationStatus status) {
        ReservationStatus currentStatus = reservation.getReservationStatus();

        switch (currentStatus) {
            case PENDING, WAITLIST -> {
                // allowed
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid state transition"
            );
        }

        if (currentStatus == ReservationStatus.WAITLIST && status == ReservationStatus.CONFIRMED) {
            boolean exists = reservationRepository.existsOverlappingReservation(
                    reservation.getId(),
                    reservation.getBungalowId(),
                    reservation.getArrivalDate(),
                    reservation.getDepartureDate()
            );

            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot confirm due to overlapping reservation"
                );
            }
        }
    }
}
