package com.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CancellationPolicyRequest {

    @NotBlank
    private String name;

    @Min(0)
    private int daysBeforeCheckInFrom;

    @Min(0)
    private int daysBeforeCheckInTo;

    @Min(0)
    @Max(100)
    private double refundPercentage;

}