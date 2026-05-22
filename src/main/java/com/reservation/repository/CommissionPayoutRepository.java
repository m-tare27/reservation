package com.reservation.repository;

import com.reservation.entity.CommissionPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionPayoutRepository extends JpaRepository<CommissionPayout, Integer> {
}
