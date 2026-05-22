package com.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @OneToOne
    @JoinColumn(name = "reservation_id",
            nullable = false,
            unique = true)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "travel_agent_id")
    private TravelAgent travelAgent;

    @OneToOne(mappedBy = "commission",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private CommissionPayout payout;
}
