package com.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class TravelAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @NotBlank
    @DecimalMin("0.0")
    @DecimalMax("20.0")
    private Double commissionRate;

    @OneToMany(mappedBy = "travelAgent")
    private List<Commission> commissions;
}
