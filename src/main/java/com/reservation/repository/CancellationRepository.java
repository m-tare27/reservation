package com.reservation.repository;

import com.reservation.entity.Cancellation;
import com.reservation.enums.RefundStatus;
import com.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationRepository  extends JpaRepository<Cancellation , Integer> {
    boolean existsByCancellationPolicy_Id(Integer policyId);

    Optional<Cancellation> findByReservation(Reservation reservation);

    List<Cancellation> findByCancelledAt(LocalDateTime time);

    List<Cancellation> findByDaysBeforeCheckInEquals(long days);

    List<Cancellation> findByRefundStatus(RefundStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cancellation c WHERE c.id = :id")
    Optional<Cancellation> findByIdForUpdate(@Param("id") Integer id);
}
