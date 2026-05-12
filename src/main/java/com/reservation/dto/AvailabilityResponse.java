package com.reservation.dto;

import com.reservation.entity.Availability;
import com.reservation.enums.AvailabilityStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityResponse {

    private Integer id;

    private LocalDate startDate;

    private LocalDate endDate;

    private AvailabilityStatus status;

    public AvailabilityResponse(Availability availability) {
        this.id = availability.getId();
        this.startDate = availability.getStartDate();
        this.endDate = availability.getEndDate();
        this.status = availability.getStatus();
    }
}