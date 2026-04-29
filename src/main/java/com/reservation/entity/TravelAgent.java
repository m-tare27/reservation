package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class TravelAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Double commissionRate;

    @OneToMany(mappedBy = "travelAgent")
    private List<Commission> commissions;
}
