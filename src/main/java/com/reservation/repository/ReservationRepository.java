package com.reservation.repository;

import com.reservation.entity.Reservation;
import com.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    public List<Reservation> findByReservationStatus(ReservationStatus reservationStatus);
    public List<Reservation> findByBungalowId(Integer id);
}
