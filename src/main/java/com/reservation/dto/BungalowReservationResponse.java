package com.reservation.dto;

import com.reservation.entity.Bungalow;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BungalowReservationResponse {

    private Integer id;

    private String name;

    private String description;

    private BigDecimal pricePerNight;

    public BungalowReservationResponse(Bungalow bungalow) {
        this.id = bungalow.getId();
        this.name = bungalow.getName();
        this.description = bungalow.getDescription();
        this.pricePerNight = bungalow.getPricePerNight();
    }
}
