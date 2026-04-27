package com.reservation.controller;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.service.CancellationPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancellation-policies")
@RequiredArgsConstructor
public class CancellationPolicyController {
    private final CancellationPolicyService cancellationPolicyService;

    @PostMapping
    public ResponseEntity<CancellationPolicyResponse> createCancellationPolicy(
            @Valid @RequestBody CancellationPolicyRequest request) {

        CancellationPolicyResponse response = cancellationPolicyService.createCancellationPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CancellationPolicyResponse> updateCancellationPolicy(
            @PathVariable Integer id,
            @Valid @RequestBody CancellationPolicyRequest request) {

        CancellationPolicyResponse response = cancellationPolicyService.updateCancellationPolicy(id, request);
        return ResponseEntity.ok(response);  // 200 OK, not CREATED
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCancellationPolicy(@PathVariable Integer id) {
        cancellationPolicyService.deleteCancellationPolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CancellationPolicyResponse>> getCancellationPolicies() {
        List<CancellationPolicyResponse> response = cancellationPolicyService.getCancellationPolicies();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CancellationPolicyResponse> getCancellationPolicyById(@PathVariable Integer id) {
        CancellationPolicyResponse response = cancellationPolicyService.getCancellationPolicyById(id);
        return ResponseEntity.ok(response);
    }

}
