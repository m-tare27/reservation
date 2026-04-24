package com.reservation.dto;

import com.reservation.entity.Guest;
import lombok.Data;

@Data
public class GuestResponse {
    private Integer id;
    private String name;
    private String email;
    private int loyaltyPoints;

    public GuestResponse(Guest guest) {
        this.id = guest.getId();
        this.name = guest.getName();
        this.email = guest.getEmail();
        this.loyaltyPoints = guest.getLoyaltyPoints();
    }
}
