package com.reservation.dto;

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
}