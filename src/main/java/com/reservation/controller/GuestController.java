package com.reservation.controller;

import com.reservation.dto.GuestRequest;
import com.reservation.dto.GuestResponse;
import com.reservation.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
@Tag(name = "Guest Management", description = "APIs for managing guests")
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    @Operation(summary = "Create a new guest", description = "Creates a new guest with the provided details")
    public ResponseEntity<GuestResponse> createGuest(@Valid @RequestBody GuestRequest request) {
        GuestResponse response = guestService.createGuest(request);
        return ResponseEntity.status(
                HttpStatus.CREATED
        ).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update guest details", description = "Updates the details of an existing guest by ID")
    public ResponseEntity<GuestResponse> updateGuest(
            @PathVariable Integer id,
            @Valid @RequestBody GuestRequest request) {
        GuestResponse response = guestService.updateGuest(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get guest by ID", description = "Retrieves the details of a guest by their ID")
    public ResponseEntity<GuestResponse> getGuestById(@PathVariable Integer id) {
        GuestResponse response = guestService.getGuestById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get guest by email", description = "Retrieves the details of a guest by their email address")
    public ResponseEntity<GuestResponse> getGuestByEmail(@RequestParam String email) {
        GuestResponse response = guestService.getGuestByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all guests", description = "Retrieves a list of all guests")
    public ResponseEntity<List<GuestResponse>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

}
