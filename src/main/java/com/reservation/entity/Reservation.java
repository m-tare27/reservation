package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
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

    @Enumerated(EnumType.STRING)
    ReservationStatus reservationStatus;

    LocalDateTime createdAt;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Cancellation cancellation;
}
