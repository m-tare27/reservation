package com.reservation.repository;

import com.reservation.entity.Bungalow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BungalowRepository extends JpaRepository<Bungalow , Integer> {
}
