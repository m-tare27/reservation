package com.reservation.repository;

import com.reservation.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Integer> {

    boolean existsByReservationId(Integer reservationId);

    Optional<Commission> findByReservationId(Integer reservationId);
}
