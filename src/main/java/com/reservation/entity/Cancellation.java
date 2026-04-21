package com.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Cancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDateTime cancelledAt;

    long daysBeforeCheckIn;

    double refundAmount;

    @Enumerated(EnumType.STRING)
    RefundStatus refundStatus;

    String reason;

    @OneToOne(mappedBy = "cancellation")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "cancellation_policy_id")
    private CancellationPolicy cancellationPolicy;
}
