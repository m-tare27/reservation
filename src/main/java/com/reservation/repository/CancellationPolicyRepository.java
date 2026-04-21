package com.reservation.repository;

import com.reservation.entity.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy , Integer> {

    @Query("""
    SELECT COUNT(p) > 0
    FROM CancellationPolicy p
    WHERE (:excludeId IS NULL OR p.id != :excludeId)
    AND (:from <= p.daysBeforeCheckInTo AND :to >= p.daysBeforeCheckInFrom)
    """)
    boolean existsOverlappingRange(int from, int to, Integer excludeId);

    @Query("""
    SELECT p FROM CancellationPolicy p
    WHERE :days BETWEEN p.daysBeforeCheckInFrom AND p.daysBeforeCheckInTo
""")
    Optional<CancellationPolicy> findApplicablePolicy(long days);
}
