package com.reservation.repository;

import com.reservation.dto.CancellationResponse;
import com.reservation.entity.Cancellation;
import com.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancellationRepository  extends JpaRepository<Cancellation , Integer> {
    boolean existsByCancellationPolicy_Id(Integer policyId);

    Optional<Cancellation> findByReservation(Reservation reservation);
}
