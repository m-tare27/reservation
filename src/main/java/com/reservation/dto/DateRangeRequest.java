package com.reservation.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRangeRequest {
    LocalDate startDate , endDate;
}
