package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    String email;

    int loyaltyPoints;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    List<Reservation> reservations;
}
