package com.reservation.service;

import com.reservation.dto.GuestRequest;
import com.reservation.dto.GuestResponse;
import com.reservation.entity.Guest;
import com.reservation.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestResponse createGuest(String name, String email) {
        var guest = new Guest();
        guest.setName(name);
        guest.setEmail(email);
        guest.setLoyaltyPoints(0); // New guests start with 0 loyalty points
        var savedGuest = guestRepository.save(guest);
        return new GuestResponse(savedGuest);
    }

    public GuestResponse updateGuest(Integer id, GuestRequest request) {
        var guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        var updatedGuest = guestRepository.save(guest);
        return new GuestResponse(updatedGuest);
    }

    public GuestResponse getGuestById(Integer id) {
        var guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        return new GuestResponse(guest);
    }

    public GuestResponse getGuestByEmail(String email) {
        var guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        return new GuestResponse(guest);
    }
}
