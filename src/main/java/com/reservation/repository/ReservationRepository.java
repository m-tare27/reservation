package com.reservation.repository;

import com.reservation.entity.Reservation;
import com.reservation.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends
        JpaRepository<Reservation, Integer> , JpaSpecificationExecutor<Reservation> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Integer id);

    List<Reservation> findByBungalow_IdAndReservationStatusOrderByCreatedAtAsc(Integer bungalowId, ReservationStatus reservationStatus);

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reservation r
    WHERE (:excludeId IS NULL OR r.id != :excludeId) AND
    (r.bungalow.id = :bungalowId
      AND :arrivalDate < r.departureDate
      AND :departureDate > r.arrivalDate
      AND r.reservationStatus IN ('PENDING', 'CONFIRMED'))
      
""")
    boolean existsOverlappingReservation(
            @Param("excludeId") Integer excludeId,
            @Param("bungalowId") Integer bungalowId,
            @Param("arrivalDate") LocalDate arrivalDate,
            @Param("departureDate") LocalDate departureDate
    );
}
