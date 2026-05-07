package com.reservation.service;

import com.reservation.dto.GuestRequest;
import com.reservation.dto.GuestResponse;
import com.reservation.entity.Guest;
import com.reservation.entity.Reservation;
import com.reservation.repository.GuestRepository;
import com.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public GuestResponse createGuest(GuestRequest request) {

        if (guestRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Guest with email " + request.getEmail() + " already exists");
        }

        Guest guest = new Guest();
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setLoyaltyPoints(0);

        Guest savedGuest = guestRepository.save(guest);

        return new GuestResponse(savedGuest);
    }

    public GuestResponse updateGuest(Integer id, GuestRequest request) {

        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Guest with id " + id + " not found"));

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

    public List<GuestResponse> getAllGuests() {
        return guestRepository.findAll()
                .stream()
                .map(GuestResponse::new)
                .toList();
    }

    public void addLoyaltyPoints(Integer guestId , Double totalAmount) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest with id " + guestId + " not found"));

        int pointsToAdd = (int) (totalAmount / 10);
        guest.setLoyaltyPoints(
                guest.getLoyaltyPoints() + pointsToAdd
        );
        guestRepository.save(guest);
    }
}
