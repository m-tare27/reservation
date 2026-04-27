package com.reservation.dto;

import com.reservation.entity.Guest;
import lombok.Data;

@Data
public class GuestReservationResponse {
    private Integer guestId;
    private String name;
    private String email;

    public GuestReservationResponse(Guest guest) {
        this.guestId = guest.getId();
        this.name = guest.getName();
        this.email = guest.getEmail();
    }
}
