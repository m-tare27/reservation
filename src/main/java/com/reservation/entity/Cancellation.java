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

    @OneToOne(mappedBy = "reservationId")
    Reservation reservation;

    @OneToOne(mappedBy = "cancellationPolicyId")
    CancellationPolicy cancellationPolicy;

    LocalDateTime cancelledAt;

    int daysBeforeCheckIn;

    double refundAmount;

    RefundStatus refundStatus;

    String Reason;
}
