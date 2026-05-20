package com.reservation.dto;

import com.reservation.entity.Reservation;
import com.reservation.enums.ReservationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {

    private Integer reservationId;

    private BungalowResponse bungalowResponse;

    private GuestReservationResponse guest;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private BigDecimal totalAmount;

    private ReservationStatus reservationStatus;

    private LocalDateTime createdAt;

    public ReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.bungalowResponse = new BungalowResponse(reservation.getBungalow());
        this.guest = new GuestReservationResponse(reservation.getGuest());
        this.arrivalDate = reservation.getArrivalDate();
        this.departureDate = reservation.getDepartureDate();
        this.totalAmount = reservation.getTotalAmount();
        this.reservationStatus = reservation.getReservationStatus();
        this.createdAt = reservation.getCreatedAt();
    }
}