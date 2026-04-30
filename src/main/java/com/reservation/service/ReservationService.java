package com.reservation.service;

import com.reservation.dto.ReservationRequest;
import com.reservation.dto.ReservationResponse;
import com.reservation.entity.*;
import com.reservation.mapper.Mapper;
import com.reservation.repository.*;
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
    private final TravelAgentRepository travelAgentRepository;
    private final CommissionRepository commissionRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public ReservationResponse createReservation(ReservationRequest request){
        Commission savedCommission = null;
        Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with email " + request.getGuestEmail() + " not found"));

        if (isInvalidReservationDate(request.getArrivalDate(), request.getDepartureDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival date must be today or later, and departure must be after arrival");
        }

        if (request.getBookingSource() == BookingSource.TRAVEL_AGENCY) {
            if (request.getTravelAgentId() == null){
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Travel agent ID is required when booking source is TRAVEL_AGENCY");
            }
            TravelAgent travelAgent = travelAgentRepository.findById(request.getTravelAgentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Travel agent with id " + request.getTravelAgentId() + " not found"));

            Commission commission = new Commission();
            commission.setAmount(request.getTotalAmount() * travelAgent.getCommissionRate()/100.0);
            commission.setTravelAgent(travelAgent);
            savedCommission = commissionRepository.save(commission);
        }


        Reservation reservation = new Reservation();
        Mapper.mapRequestToEntity(reservation , request , guest);

        boolean exists = reservationRepository.existsOverlappingReservation(null , request.getBungalowId() , request.getArrivalDate() , request.getDepartureDate());
        reservation.setReservationStatus(exists ? ReservationStatus.WAITLIST : ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        if (savedCommission != null) {
            reservation.setCommission(savedCommission);
            savedCommission.setReservation(savedReservation);
        }

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

        if (reservation.getReservationStatus() == ReservationStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Confirmed reservations cannot be updated");
        }

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
            boolean hasConfirmedReservation = reservationRepository.existsConfirmedReservationForSameBungalow(
                    id,
                    reservation.getArrivalDate(),
                    reservation.getDepartureDate());

            if (hasConfirmedReservation) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot confirm: another confirmed reservation exists for this bungalow during these dates");
            }
        }

        reservation.setReservationStatus(status);
        reservationRepository.save(reservation);

        Guest guest = reservation.getGuest();
        if (status == ReservationStatus.CONFIRMED){
            int loyaltyPoints = (int) reservation.getTotalAmount() / 10;
            guest.setLoyaltyPoints(guest.getLoyaltyPoints() + loyaltyPoints);

            if (reservation.getBookingSource() == BookingSource.TRAVEL_AGENCY) {
                Commission commission = reservation.getCommission();
                Payment payment = new Payment();
                payment.setAmount(commission.getAmount());
                payment.setPaymentStatus(PaymentStatus.PENDING);
                payment.setCommission(commission);

                Payment savedPayment = paymentRepository.save(payment);
                if (commission.getPayments() != null)
                    commission.getPayments().add(savedPayment);
                else
                    commission.setPayments(List.of(savedPayment));

            }

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
