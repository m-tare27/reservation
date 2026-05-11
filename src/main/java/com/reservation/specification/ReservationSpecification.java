package com.reservation.specification;

import com.reservation.entity.Reservation;
import com.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ReservationSpecification {

    public static Specification<Reservation> hasId(Integer id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("id"), id);
    }

    public static Specification<Reservation> hasBungalowId(Integer bungalowId) {
        return (root, query, cb) ->
                bungalowId == null ? null :
                        cb.equal(root.get("bungalowId"), bungalowId);
    }

    public static Specification<Reservation> hasStatus(
            ReservationStatus status
    ) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("reservationStatus"), status);
    }

    public static Specification<Reservation> hasDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        return (root, query, cb) -> {

            if (startDate == null || endDate == null) {
                return null;
            }

            return cb.and(
                    cb.lessThanOrEqualTo(
                            root.get("arrivalDate"),
                            endDate
                    ),
                    cb.greaterThanOrEqualTo(
                            root.get("departureDate"),
                            startDate
                    )
            );
        };
    }
}