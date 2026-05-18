package com.reservation.mapper;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.ReservationRequest;
import com.reservation.entity.Bungalow;
import com.reservation.entity.CancellationPolicy;
import com.reservation.entity.Guest;
import com.reservation.entity.Reservation;

import java.time.LocalDateTime;

public class Mapper {

    static public void mapRequestToEntity(CancellationPolicy cancellationPolicy , CancellationPolicyRequest policy){
        cancellationPolicy.setName(policy.getName());
        cancellationPolicy.setDaysBeforeCheckInFrom(policy.getDaysBeforeCheckInFrom());
        cancellationPolicy.setDaysBeforeCheckInTo(policy.getDaysBeforeCheckInTo());
        cancellationPolicy.setRefundPercentage(policy.getRefundPercentage());
    }

    public static void mapRequestToEntity(Reservation reservation, ReservationRequest request, Guest guest, Bungalow bungalow , double totalAmount) {
        reservation.setGuest(guest);
        reservation.setBungalow(bungalow);
        reservation.setArrivalDate(request.getArrivalDate());
        reservation.setDepartureDate(request.getDepartureDate());
        reservation.setTotalAmount(totalAmount);
        reservation.setBookingSource(request.getBookingSource());
        reservation.setCreatedAt(LocalDateTime.now());
    }
}
