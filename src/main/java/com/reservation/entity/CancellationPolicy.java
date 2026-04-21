package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class CancellationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    int daysBeforeCheckInFrom;

    int daysBeforeCheckInTo;

    double refundPercentage;

    @OneToMany(mappedBy = "cancellationPolicy")
    private List<Cancellation> cancellations;
}
