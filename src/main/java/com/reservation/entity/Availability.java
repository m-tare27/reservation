package com.reservation.entity;

import com.reservation.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "bungalow_id")
    private Bungalow bungalow;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus status;

    @ManyToOne
    private Reservation reservation;
}
