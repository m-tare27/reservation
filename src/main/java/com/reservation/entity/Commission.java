package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double amount;

    @OneToOne(mappedBy = "commission")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "travel_agent_id")
    private TravelAgent travelAgent;
}
