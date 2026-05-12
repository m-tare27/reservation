package com.reservation.dto;

import com.reservation.entity.Bungalow;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BungalowResponse {

    private Integer id;

    private String name;

    private String description;

    private BigDecimal pricePerNight;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<AvailabilityResponse> availabilities;


    public BungalowResponse(Bungalow bungalow) {
        this.id = bungalow.getId();
        this.name = bungalow.getName();
        this.description = bungalow.getDescription();
        this.pricePerNight = bungalow.getPricePerNight();
        this.createdAt = bungalow.getCreatedAt();
        this.updatedAt = bungalow.getUpdatedAt();

        this.availabilities = bungalow.getAvailabilities() != null
                ? bungalow.getAvailabilities()
                .stream()
                .map(AvailabilityResponse::new)
                .toList()
                : List.of();
    }
}
