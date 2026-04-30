package com.reservation.service;

import com.reservation.dto.TravelAgentRequest;
import com.reservation.entity.TravelAgent;
import com.reservation.repository.TravelAgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelAgentService {

    private final TravelAgentRepository travelAgentRepository;

    public TravelAgent createTravelAgent(TravelAgentRequest request) {
        validateTravelAgent(request);

        TravelAgent travelAgent = new TravelAgent();
        travelAgent.setName(request.getName());
        travelAgent.setCommissionRate(request.getCommissionRate());
        return travelAgentRepository.save(travelAgent);
    }

    public TravelAgent getTravelAgentById(Integer id) {
        return travelAgentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Travel Agent not found"));
    }

    public TravelAgent updateTravelAgent(Integer id, TravelAgentRequest updatedTravelAgent) {
        validateTravelAgent(updatedTravelAgent);

        TravelAgent existingTravelAgent = getTravelAgentById(id);

        existingTravelAgent.setName(updatedTravelAgent.getName());
        existingTravelAgent.setCommissionRate(updatedTravelAgent.getCommissionRate());

        return travelAgentRepository.save(existingTravelAgent);
    }

    public List<TravelAgent> getAllTravelAgents() {
        return travelAgentRepository.findAll();
    }

    // Helper methods

    private void validateTravelAgent(TravelAgentRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }

        Double rate = request.getCommissionRate();
        if (rate == null || rate < 0 || rate > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Commission rate must be between 0% and 20%");
        }
    }
}
