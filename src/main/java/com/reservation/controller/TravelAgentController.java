package com.reservation.controller;

import com.reservation.dto.TravelAgentRequest;
import com.reservation.entity.TravelAgent;
import com.reservation.service.TravelAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-agents")
@RequiredArgsConstructor
@Tag(name = "Travel Agent Management", description = "Endpoints for managing travel agents")
public class TravelAgentController {

    private final TravelAgentService travelAgentService;

    @GetMapping()
    @Operation(summary = "Get all travel agents", description = "Retrieve a list of all travel agents")
    public List<TravelAgent> getAllTravelAgents() {
        return travelAgentService.getAllTravelAgents();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get travel agent by ID", description = "Retrieve a travel agent by their unique ID")
    public TravelAgent getTravelAgentById(@PathVariable Integer id) {
        return travelAgentService.getTravelAgentById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update travel agent", description = "Update the details of an existing travel agent")
    public TravelAgent updateTravelAgent(
            @PathVariable Integer id,
            @Valid @RequestBody TravelAgentRequest updatedTravelAgent) {
        return travelAgentService.updateTravelAgent(id, updatedTravelAgent);
    }

    @PostMapping()
    @Operation(summary = "Create new travel agent", description = "Create a new travel agent with the provided details")
    public TravelAgent createTravelAgent(@Valid @RequestBody TravelAgentRequest travelAgent) {
        return travelAgentService.createTravelAgent(travelAgent);
    }
}
