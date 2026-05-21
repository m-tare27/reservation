package com.reservation.service;

import com.reservation.config.RabbitConfig;
import com.reservation.dto.event.ReservationCompletedEvent;
import com.reservation.dto.event.ReservationConfirmedEvent;
import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.dto.event.ReservationCreationEvent;
import com.reservation.dto.event.ReservationExpiredEvent;
import com.reservation.entity.*;
import com.reservation.enums.BookingSource;
import com.reservation.enums.ReservationStatus;
import com.reservation.mapper.Mapper;
import com.reservation.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.reservation.specification.ReservationSpecification.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    public  final BungalowRepository bungalowRepository;
    private final AvailabilityRepository availabilityRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationEventPublisher eventPublisher;

    private final AvailabilityService availabilityService;

    public ReservationResponse createReservation(ReservationRequest request){
        validateReservation(request);
        Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with email " + request.getGuestEmail() + " not found"));

        Bungalow bungalow = bungalowRepository.findById(request.getBungalowId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bungalow with Id " + request.getBungalowId() + " not found"));

        long nights = ChronoUnit.DAYS.between(
                request.getArrivalDate(),
                request.getDepartureDate()
        );

        BigDecimal totalAmount = bungalow.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation();
        Mapper.mapRequestToEntity(reservation , request , guest , bungalow , totalAmount);

        boolean available = availabilityRepository.findAvailableInterval(request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate()).isPresent();
        reservation.setReservationStatus(available ? ReservationStatus.PENDING : ReservationStatus.WAITLIST);

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

        if (available)
            availabilityService.reserveInterval(savedReservation);

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse updateReservation(ReservationRequest request , Integer id){
        Reservation reservation = reservationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation with id " + id + " not found"));


        validateReservation(reservation , request);

        Mapper.mapRequestToEntity(reservation , request , reservation.getGuest() , reservation.getBungalow() , reservation.getTotalAmount());
        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponse(savedReservation);
    }

    public void confirmReservationStatus(Integer id){
        Reservation reservation = reservationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"));

        validateReservationConfirmation(reservation);

        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
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

    public List<ReservationResponse> getReservations(
            Integer id,
            Integer bungalowId,
            Integer guestId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must be before end date"
            );
        }

        Specification<Reservation> specification =
                Specification
                        .where(hasId(id))
                        .and(hasBungalowId(bungalowId))
                        .and(hasGuestId(guestId))
                        .and(hasStatus(status))
                        .and(hasDateRange(startDate, endDate));

        return reservationRepository.findAll(specification)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public ReservationResponse completeReservation(Integer id) {
        Reservation reservation = reservationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"));

        if (reservation.getReservationStatus()
                != ReservationStatus.CONFIRMED)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only confirmed reservations can be completed" );
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

    public void expireReservation(Reservation item) {
        if (item.getReservationStatus()
                != ReservationStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending reservations can expire"
            );
        }

        item.setReservationStatus(ReservationStatus.EXPIRED);
        reservationRepository.save(item);

        ReservationExpiredEvent reservationExpiredEvent = new ReservationExpiredEvent(
                item.getId(),
                item.getBungalow().getId()
        );

        eventPublisher.publishEvent(reservationExpiredEvent);

    }
        //Helper methods

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

    public void validateReservation(
            Reservation reservation,
            ReservationRequest request
    ) {

        if (isInvalidReservationDate(
                request.getArrivalDate(),
                request.getDepartureDate()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date range provided"
            );
        }

        if (reservation.getReservationStatus()
                == ReservationStatus.CONFIRMED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Confirmed reservations cannot be updated"
            );
        }

        boolean occupancyChanged =
                !reservation.getBungalow().getId().equals(request.getBungalowId())
                        || !reservation.getArrivalDate().equals(request.getArrivalDate())
                        || !reservation.getDepartureDate().equals(request.getDepartureDate());

        boolean shouldValidateOverlap =
                reservation.getReservationStatus()
                        != ReservationStatus.WAITLIST
                        || occupancyChanged;

        if (shouldValidateOverlap) {

            boolean exists =
                    reservationRepository.existsOverlappingReservation(
                            reservation.getId(),
                            request.getBungalowId(),
                            request.getArrivalDate(),
                            request.getDepartureDate()
                    );

            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Bungalow is booked for those dates"
                );
            }
        }
    }

    public void validateReservationConfirmation(Reservation reservation) {
        ReservationStatus currentStatus = reservation.getReservationStatus();

        if (currentStatus != ReservationStatus.PENDING &&
                currentStatus != ReservationStatus.WAITLIST) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending or waitlisted reservations can be confirmed"
            );
        }

        if (reservation.getArrivalDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Past reservations cannot be confirmed"
            );
        }

        if (currentStatus == ReservationStatus.WAITLIST) {
            boolean exists = reservationRepository.existsOverlappingReservation(
                    reservation.getId(),
                    reservation.getBungalow().getId(),
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
