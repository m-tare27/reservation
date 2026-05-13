package com.reservation.repository;

import com.reservation.entity.Availability;
import com.reservation.entity.Bungalow;
import com.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability , Integer> {

    @Query("""
    SELECT a
    FROM Availability a
    WHERE a.bungalow.id = :bungalowId
    AND a.status = 'AVAILABLE'
    AND a.startDate <= :startDate
    AND a.endDate >= :endDate
""")
    Optional<Availability> findAvailableInterval(
            @Param("bungalowId") Integer bungalowId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<Availability> findByReservation(Reservation reservation);

    List<Availability> findByBungalowOrderByStartDateAsc(
            Bungalow bungalow
    );

}
