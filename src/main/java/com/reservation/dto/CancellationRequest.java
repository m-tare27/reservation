package com.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancellationRequest {
    @NotNull(message = "Reservation ID required")
    @Positive(message = "Reservation ID must be positive")
    private Integer id;

    @NotBlank(message = "Cancellation reason required")
    @Size(min = 3, max = 500, message = "Reason must be 3-500 characters")
    private String reason;
}
