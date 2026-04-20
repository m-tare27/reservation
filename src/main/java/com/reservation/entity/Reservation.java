package com.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Integer bungalowId;

    String guestName;

    String guestEmail;

    LocalDate arrivalDate;

    LocalDate departureDate;

    double totalAmount;

    ReservationStatus reservationStatus;

    LocalDateTime createdAt;
}
