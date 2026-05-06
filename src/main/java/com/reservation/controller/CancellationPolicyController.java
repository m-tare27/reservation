package com.reservation.controller;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.service.CancellationPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancellation-policies")
@RequiredArgsConstructor
@Tag(name = "Cancellation Policy API", description = "APIs for managing cancellation policies")
public class CancellationPolicyController {
    private final CancellationPolicyService cancellationPolicyService;

    @PostMapping
    @Operation(summary = "Create a new cancellation policy", description = "Creates a new cancellation policy with the provided details.")
    public ResponseEntity<CancellationPolicyResponse> createCancellationPolicy(
            @Valid @RequestBody CancellationPolicyRequest request) {

        CancellationPolicyResponse response = cancellationPolicyService.createCancellationPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing cancellation policy", description = "Updates the cancellation policy with the specified ID using the provided details.")
    public ResponseEntity<CancellationPolicyResponse> updateCancellationPolicy(
            @PathVariable Integer id,
            @Valid @RequestBody CancellationPolicyRequest request) {

        CancellationPolicyResponse response = cancellationPolicyService.updateCancellationPolicy(id, request);
        return ResponseEntity.ok(response);  // 200 OK, not CREATED
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a cancellation policy", description = "Deletes the cancellation policy with the specified ID.")
    public ResponseEntity<Void> deleteCancellationPolicy(@PathVariable Integer id) {
        cancellationPolicyService.deleteCancellationPolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all cancellation policies", description = "Retrieves a list of all cancellation policies.")
    public ResponseEntity<List<CancellationPolicyResponse>> getCancellationPolicies() {
        List<CancellationPolicyResponse> response = cancellationPolicyService.getCancellationPolicies();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a cancellation policy by ID", description = "Retrieves the cancellation policy with the specified ID.")
    public ResponseEntity<CancellationPolicyResponse> getCancellationPolicyById(@PathVariable Integer id) {
        CancellationPolicyResponse response = cancellationPolicyService.getCancellationPolicyById(id);
        return ResponseEntity.ok(response);
    }

}
