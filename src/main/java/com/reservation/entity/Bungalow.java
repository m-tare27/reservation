package com.reservation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Bungalow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    private String name;

    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "bungalow")
    private List<Availability> availabilities;

    @OneToMany(mappedBy = "bungalow")
    private List<Reservation> reservations;
}
