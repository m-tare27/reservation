package com.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {

    @NotNull
    private Integer bungalowId;

    @NotBlank
    private String guestName;

    @Email
    @NotBlank
    private String guestEmail;

    @NotNull
    private LocalDate arrivalDate;

    @NotNull
    private LocalDate departureDate;

    @Positive
    private double totalAmount;
}