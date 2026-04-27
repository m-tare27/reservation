package com.reservation.repository;

import com.reservation.entity.Payment;
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

    default Integer findReservationIdById(Integer id) {
        return findById(id).map(payment -> payment.getReservation().getId()).orElse(null);
    }
}
