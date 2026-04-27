package com.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CancellationPolicyRequest {

    @NotBlank
    private String name;

    @NotNull(message = "Days before check-in from is required")
    @Min(value = 0, message = "Must be non-negative")
    private Long daysBeforeCheckInFrom;

    @NotNull(message = "Days before check-in to is required")
    @Min(value = 1, message = "Must be positive")
    private Long daysBeforeCheckInTo;

    @NotNull(message = "Refund percentage is required")
    @DecimalMin(value = "0.0", message = "Must be at least 0")
    @DecimalMax(value = "100.0", message = "Cannot exceed 100")
    private Double refundPercentage;

}