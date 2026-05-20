package com.reservation.entity;

import com.reservation.enums.RefundStatus;
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

    Double refundAmount;

    @Enumerated(EnumType.STRING)
    RefundStatus refundStatus;

    String reason;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "cancellation_policy_id")
    private CancellationPolicy cancellationPolicy;
}
