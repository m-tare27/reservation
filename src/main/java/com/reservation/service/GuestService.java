package com.reservation.service;

import com.reservation.dto.GuestRequest;
import com.reservation.dto.GuestResponse;
import com.reservation.entity.Guest;
import com.reservation.repository.GuestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestResponse createGuest(String name, String email) {

        if (guestRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Guest with email " + email + " already exists");
        }

        Guest guest = new Guest();
        guest.setName(name);
        guest.setEmail(email);
        guest.setLoyaltyPoints(0);

        Guest savedGuest = guestRepository.save(guest);

        return new GuestResponse(savedGuest);
    }

    public GuestResponse updateGuest(Integer id, GuestRequest request) {

        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        if (!guest.getEmail().equals(request.getEmail()) &&
                guestRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another guest with email " + request.getEmail() + " already exists");
        }

        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        Guest updatedGuest = guestRepository.save(guest);
        return new GuestResponse(updatedGuest);
    }

    public GuestResponse getGuestById(Integer id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with id " + id + " not found"));
        return new GuestResponse(guest);
    }

    public GuestResponse getGuestByEmail(String email) {
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with email " + email + " not found"));
        return new GuestResponse(guest);
    }
}
