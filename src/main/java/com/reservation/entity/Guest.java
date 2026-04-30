package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    int loyaltyPoints = 0;

    @OneToMany(mappedBy = "guest", cascade = {CascadeType.MERGE , CascadeType.PERSIST}, fetch = FetchType.LAZY , orphanRemoval = true)
    List<Reservation> reservations = new ArrayList<>();
}
