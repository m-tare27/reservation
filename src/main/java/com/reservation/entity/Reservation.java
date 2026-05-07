package com.reservation.entity;

import com.reservation.enums.BookingSource;
import com.reservation.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Integer bungalowId;

    LocalDate arrivalDate;

    LocalDate departureDate;

    double totalAmount;

    @Enumerated(EnumType.STRING)
    ReservationStatus reservationStatus;

    @Enumerated(EnumType.STRING)
    BookingSource bookingSource;

    LocalDateTime createdAt;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Cancellation cancellation;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    Guest guest;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    List<Payment> payments;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "commission_id")
    private Commission commission;
}
