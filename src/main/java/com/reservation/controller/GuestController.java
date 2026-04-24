package com.reservation.controller;

import com.reservation.dto.GuestRequest;
import com.reservation.dto.GuestResponse;
import com.reservation.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping("/create")
    public GuestResponse createGuest(GuestRequest request) {
        return guestService.createGuest(request.getName(), request.getEmail());
    }

    @PutMapping("update/{id}")
    public GuestResponse updateGuest( @PathVariable Integer id, @RequestBody GuestRequest request) {
        return guestService.updateGuest(id, request);
    }

    @GetMapping("/{id}")
    public GuestResponse getGuestById(@PathVariable Integer id) {
        return guestService.getGuestById(id);
    }

    @GetMapping("/email")
    public GuestResponse getGuestByEmail(@RequestParam String email) {
        return guestService.getGuestByEmail(email);
    }

}
