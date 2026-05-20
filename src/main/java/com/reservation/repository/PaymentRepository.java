package com.reservation.repository;

import com.reservation.entity.Payment;
import com.reservation.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByReservationId(Integer reservationId);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0.0)
        FROM Payment p
        WHERE p.reservation.id = :reservationId
        AND p.paymentStatus = 'COMPLETED'""")
    Double sumPaymentsByReservationId(@Param("reservationId") Integer reservationId);

    @Query("""
    SELECT COALESCE(SUM(
        CASE
            WHEN p.paymentStatus = com.reservation.enums.PaymentStatus.COMPLETED
                THEN p.amount
            WHEN p.paymentStatus = com.reservation.enums.PaymentStatus.REFUNDED
                THEN -p.amount
            ELSE 0
        END
    ), 0.0)
    FROM Payment p
    WHERE p.reservation.bungalow.id = :bungalowId
    """)
    Double getRevenueByBungalowId(
            @Param("bungalowId") Integer bungalowId
    );
}
