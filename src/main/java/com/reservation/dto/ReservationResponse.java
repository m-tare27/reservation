package com.reservation.dto;

import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {

    private Integer id;

    private Integer bungalowId;

    private String guestName;

    private String guestEmail;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private double totalAmount;

    private ReservationStatus reservationStatus;

    private LocalDateTime createdAt;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.bungalowId = reservation.getBungalowId();
        this.guestName = reservation.getGuestName();
        this.guestEmail = reservation.getGuestEmail();
        this.arrivalDate = reservation.getArrivalDate();
        this.departureDate = reservation.getDepartureDate();
        this.totalAmount = reservation.getTotalAmount();
        this.reservationStatus = reservation.getReservationStatus();
        this.createdAt = reservation.getCreatedAt();
    }
}