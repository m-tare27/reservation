package com.reservation.repository;

import com.reservation.entity.TravelAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelAgentRepository extends JpaRepository<TravelAgent, Integer> {
}
