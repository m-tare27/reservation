package com.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private Double amount;

    @OneToOne(mappedBy = "commission")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "travel_agent_id")
    private TravelAgent travelAgent;

    @OneToMany(mappedBy = "commission", cascade = CascadeType.ALL)
    private List<Payment> payments;
}
