package com.reservation.controller;

import com.reservation.dto.CancellationRequest;
import com.reservation.dto.CancellationResponse;
import com.reservation.service.CancellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cancel")
@RequiredArgsConstructor
public class CancellationController {
    private final CancellationService cancellationService;

    @PostMapping
    public CancellationResponse cancelReservation(@RequestBody CancellationRequest request){
        return cancellationService.cancelReservation(request);
    }
}
