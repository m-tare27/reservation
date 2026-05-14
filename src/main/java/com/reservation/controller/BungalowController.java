package com.reservation.controller;

import com.reservation.dto.AvailabilityResponse;
import com.reservation.dto.BungalowRequest;
import com.reservation.dto.BungalowResponse;
import com.reservation.service.BungalowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bungalows")
@RequiredArgsConstructor
public class BungalowController {

    private final BungalowService bungalowService;

    @PostMapping
    public BungalowResponse createBungalow(
            @Valid @RequestBody BungalowRequest request
    ) {
        return bungalowService.createBungalow(request);
    }

    @PutMapping("/{bungalowId}")
    public BungalowResponse updateBungalow(
            @PathVariable Integer bungalowId,
            @Valid @RequestBody BungalowRequest request
    ) {
        return bungalowService.updateBungalow(bungalowId, request);
    }

    @GetMapping
    public List<BungalowResponse> getAllBungalows() {
        return bungalowService.getAllBungalows();
    }

    @GetMapping("/{bungalowId}")
    public BungalowResponse getBungalowById(
            @PathVariable Integer bungalowId
    ) {
        return bungalowService.getBungalowById(bungalowId);
    }

    @GetMapping("/availability/{bungalowId}")
    public List<AvailabilityResponse> getBungalowAvailability(@PathVariable Integer bungalowId){
        return bungalowService.getBungalowAvailabilities(bungalowId);
    }
}