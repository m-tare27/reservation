package com.reservation.dto;

import com.reservation.entity.BookingSource;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {

    @NotNull(message = "Bungalow ID is required")
    @Positive(message = "Bungalow ID must be a positive number")
    private Integer bungalowId;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Guest email is required")
    private String guestEmail;

    @NotNull(message = "Arrival date is required")
    @Future(message = "Arrival date must be in the future")
    private LocalDate arrivalDate;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate departureDate;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than 0")
    private double totalAmount;

    private BookingSource bookingSource;

    private Integer travelAgentId;
}