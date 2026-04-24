package com.reservation.dto;

import com.reservation.entity.Guest;
import lombok.Data;

import java.util.List;

@Data
public class GuestResponse {
    private Integer id;
    private String name;
    private String email;
    private int loyaltyPoints;
    List<ReservationResponse> reservationList;

    public GuestResponse(Guest guest) {
        this.id = guest.getId();
        this.name = guest.getName();
        this.email = guest.getEmail();
        this.loyaltyPoints = guest.getLoyaltyPoints();
        this.reservationList = guest.getReservations()
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }
}
