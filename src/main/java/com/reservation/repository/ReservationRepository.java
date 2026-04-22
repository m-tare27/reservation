package com.reservation.repository;

import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    public List<Reservation> findByReservationStatus(ReservationStatus reservationStatus);
    public List<Reservation> findByBungalowId(Integer id);
    List<Reservation> findByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(
            LocalDate endDate,
            LocalDate startDate
    );

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
