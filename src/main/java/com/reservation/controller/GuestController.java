package com.reservation.controller;

import com.reservation.dto.GuestRequest;
import com.reservation.dto.GuestResponse;
import com.reservation.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestResponse> createGuest(@RequestBody GuestRequest request) {
        GuestResponse response = guestService.createGuest(request.getName(), request.getEmail());
        return ResponseEntity.status(
                HttpStatus.CREATED
        ).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestResponse> updateGuest(
            @PathVariable Integer id,
            @RequestBody GuestRequest request) {
        GuestResponse response = guestService.updateGuest(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getGuestById(@PathVariable Integer id) {
        GuestResponse response = guestService.getGuestById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    public ResponseEntity<GuestResponse> getGuestByEmail(@RequestParam String email) {
        GuestResponse response = guestService.getGuestByEmail(email);
        return ResponseEntity.ok(response);
    }

}
