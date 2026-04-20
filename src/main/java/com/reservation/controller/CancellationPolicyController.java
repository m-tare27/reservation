package com.reservation.controller;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.service.CancellationPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policy")
@RequiredArgsConstructor
public class CancellationPolicyController {
    private final CancellationPolicyService cancellationPolicyService;

    @PostMapping("/create")
    public ResponseEntity<CancellationPolicyResponse> createPolicy(
            @Valid @RequestBody CancellationPolicyRequest request){
        CancellationPolicyResponse response = cancellationPolicyService.createCancellationPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CancellationPolicyResponse> updatePolicy(
            @Valid @RequestBody CancellationPolicyRequest request,
            @PathVariable Integer id){
        CancellationPolicyResponse response = cancellationPolicyService.updateCancellationPolicy(id , request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCancellationPolicy(@PathVariable Integer id) {
        cancellationPolicyService.deleteCancellationPolicy(id);
        return ResponseEntity.noContent().build();
    }
}
