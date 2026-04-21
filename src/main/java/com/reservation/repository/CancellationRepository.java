package com.reservation.repository;

import com.reservation.entity.Cancellation;
import com.reservation.entity.RefundStatus;
import com.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationRepository  extends JpaRepository<Cancellation , Integer> {
    boolean existsByCancellationPolicy_Id(Integer policyId);

    Optional<Cancellation> findByReservation(Reservation reservation);

    List<Cancellation> findByCancelledAt(LocalDateTime time);

    List<Cancellation> findByDaysBeforeCheckIn(long days);

    List<Cancellation> findByRefundStatus(RefundStatus status);
}
