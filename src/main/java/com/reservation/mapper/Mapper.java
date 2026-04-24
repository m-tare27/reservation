package com.reservation.mapper;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.ReservationRequest;
import com.reservation.entity.CancellationPolicy;
import com.reservation.entity.RefundStatus;
import com.reservation.entity.Reservation;

import java.time.LocalDateTime;

public class Mapper {
    static public void mapRequestToEntity(Reservation reservation, ReservationRequest request) {
        reservation.setBungalowId(request.getBungalowId());
        //reservation.setGuestName(request.getGuestName());
        //reservation.setGuestEmail(request.getGuestEmail());
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        reservation.setTotalAmount(request.getTotalAmount());
    }

    static public void mapRequestToEntity(CancellationPolicy cancellationPolicy , CancellationPolicyRequest policy){
        cancellationPolicy.setName(policy.getName());
        cancellationPolicy.setDaysBeforeCheckInFrom(policy.getDaysBeforeCheckInFrom());
        cancellationPolicy.setDaysBeforeCheckInTo(policy.getDaysBeforeCheckInTo());
        cancellationPolicy.setRefundPercentage(policy.getRefundPercentage());
    }
}
