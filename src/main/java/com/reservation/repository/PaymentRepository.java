package com.reservation.repository;

import com.reservation.entity.Payment;
import com.reservation.entity.PaymentStatus;
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
    Double sumCompletedPaymentsByReservationId(@Param("reservationId") Integer reservationId);

    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.reservation.id = :reservationId
      AND p.paymentStatus = :status
""")
    List<Payment> findByReservationIdAndStatus(
            @Param("reservationId") Integer reservationId,
            @Param("status") PaymentStatus status
    );

    @Query("""
    SELECT SUM(p.amount)
    FROM Payment p
    WHERE p.reservation.bungalowId = :bungalowId
      AND p.paymentStatus = 'COMPLETED'
""")
    Double getRevenueByBungalowId(
            @Param("bungalowId") Integer bungalowId
    );
}
