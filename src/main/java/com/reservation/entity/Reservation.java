package com.reservation.entity;

import com.reservation.enums.BookingSource;
import com.reservation.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDate arrivalDate;

    LocalDate departureDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

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

    @OneToOne(mappedBy = "reservation")
    private Commission commission;

    @ManyToOne
    @JoinColumn(name = "bungalow_id")
    Bungalow bungalow;
}
