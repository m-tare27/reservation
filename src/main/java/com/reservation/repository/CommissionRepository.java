package com.reservation.repository;

import com.reservation.entity.Commission;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Integer> {

    boolean existsByReservationId(Integer reservationId);

    Optional<Commission> findByReservationId(Integer reservationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Commission c WHERE c.id = :id")
    Optional<Commission> findByIdForUpdate(@Param("id") Integer id);
}
