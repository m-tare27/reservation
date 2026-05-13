package com.reservation.service;

import com.reservation.entity.Availability;
import com.reservation.entity.Bungalow;
import com.reservation.entity.Reservation;
import com.reservation.enums.AvailabilityStatus;
import com.reservation.repository.AvailabilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public void initializeAvailability(Bungalow bungalow) {
        Availability availability = new Availability();
        availability.setStatus(AvailabilityStatus.AVAILABLE);
        availability.setStartDate(LocalDate.now());
        availability.setEndDate(LocalDate.of(2099, 12, 31));
        availability.setBungalow(bungalow);
        availabilityRepository.save(availability);
    }

    public Availability findAvailability(
            Integer bungalowId,
            LocalDate startDate,
            LocalDate endDate) {
        return availabilityRepository.findAvailableInterval(bungalowId, startDate, endDate)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Bungalow not available for these dates"
                ));
    }

    public void reserveInterval(
            Reservation reservation
    ) {
        Bungalow bungalow = new Bungalow();
        LocalDate start = reservation.getArrivalDate();
        LocalDate end = reservation.getDepartureDate();

        Availability existing = findAvailability(
                bungalow.getId(),
                start,
                end
        );
        availabilityRepository.delete(existing);

        if(start.isAfter(existing.getStartDate())) {

            Availability before = new Availability();

            before.setBungalow(bungalow);
            before.setStatus(AvailabilityStatus.AVAILABLE);

            before.setStartDate(existing.getStartDate());
            before.setEndDate(start.minusDays(1));

            availabilityRepository.save(before);
        }

        Availability reserved = new Availability();

        reserved.setBungalow(bungalow);
        reserved.setStatus(AvailabilityStatus.RESERVED);

        reserved.setStartDate(start);
        reserved.setEndDate(end);
        reserved.setReservation(reservation);

        availabilityRepository.save(reserved);

        if(end.isBefore(existing.getEndDate())) {

            Availability after = new Availability();

            after.setBungalow(bungalow);
            after.setStatus(AvailabilityStatus.AVAILABLE);

            after.setStartDate(end.plusDays(1));
            after.setEndDate(existing.getEndDate());

            availabilityRepository.save(after);
        }
    }

    public void cancelReservation(Reservation reservation){
        Availability reserved = availabilityRepository.findByReservation(reservation)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No res interval"
                ));

        reserved.setStatus(AvailabilityStatus.AVAILABLE);
        reserved.setReservation(null);

        availabilityRepository.save(reserved);
        mergeIntervals(reserved.getBungalow());
    }

    public void mergeIntervals(Bungalow bungalow) {

        List<Availability> intervals =
                availabilityRepository
                        .findByBungalowOrderByStartDateAsc(bungalow);

        if(intervals.isEmpty()) {
            return;
        }

        List<Availability> merged = new ArrayList<>();

        Availability current = copyInterval(intervals.get(0));

        for(int i = 1; i < intervals.size(); i++) {

            Availability next = intervals.get(i);

            boolean sameStatus =
                    current.getStatus() == next.getStatus();

            boolean adjacent =
                    current.getEndDate()
                            .plusDays(1)
                            .equals(next.getStartDate());

            if(sameStatus && adjacent) {

                current.setEndDate(next.getEndDate());

            } else {
                Availability mergedInterval = copyInterval(current);
                merged.add(mergedInterval);
                current = copyInterval(next);
            }
        }

        Availability mergedInterval = copyInterval(current);

        merged.add(mergedInterval);

        availabilityRepository.deleteAll(intervals);

        availabilityRepository.saveAll(merged);
    }

    private Availability copyInterval(Availability source) {

        Availability availability = new Availability();

        availability.setBungalow(source.getBungalow());
        availability.setStartDate(source.getStartDate());
        availability.setEndDate(source.getEndDate());
        availability.setStatus(source.getStatus());
        availability.setReservation(source.getReservation());

        return availability;
    }

}
