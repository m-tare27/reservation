package com.reservation.controller;

import com.reservation.dto.TravelAgentRequest;
import com.reservation.entity.TravelAgent;
import com.reservation.service.TravelAgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-agents")
@RequiredArgsConstructor
public class TravelAgentController {

    private final TravelAgentService travelAgentService;

    @GetMapping()
    public List<TravelAgent> getAllTravelAgents() {
        return travelAgentService.getAllTravelAgents();
    }

    @GetMapping("/{id}")
    public TravelAgent getTravelAgentById(Integer id) {
        return travelAgentService.getTravelAgentById(id);
    }

    @PutMapping("/{id}")
    public TravelAgent updateTravelAgent(Integer id, TravelAgent updatedTravelAgent) {
        return travelAgentService.updateTravelAgent(id, updatedTravelAgent);
    }

    @PostMapping()
    public TravelAgent createTravelAgent(@Valid @RequestBody TravelAgentRequest travelAgent) {
        return travelAgentService.createTravelAgent(travelAgent);
    }
}
