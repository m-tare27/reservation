package com.reservation.specification;

import com.reservation.entity.Cancellation;
import com.reservation.enums.RefundStatus;
import org.springframework.data.jpa.domain.Specification;

public class CancellationSpecification {

    public static Specification<Cancellation> hasId(Integer cancellationId) {
        return (root, query, cb) ->
                cancellationId == null
                        ? null
                        : cb.equal(root.get("id"), cancellationId);
    }

    public static Specification<Cancellation> hasStatus(RefundStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(root.get("refundStatus"), status);
    }

    public static Specification<Cancellation> hasReservation(Integer reservationId) {
        return (root, query, criteriaBuilder) ->
                reservationId == null
                        ? null
                        : criteriaBuilder.equal(root.join("reservation").get("id"), reservationId);
    }

    public static Specification<Cancellation> hasCancellationPolicy(Integer cancellationPolicyId) {
        return (root, query, criteriaBuilder) ->
                cancellationPolicyId == null
                        ? null
                        : criteriaBuilder.equal(root.join("cancellationPolicy").get("id"), cancellationPolicyId);
    }
}
