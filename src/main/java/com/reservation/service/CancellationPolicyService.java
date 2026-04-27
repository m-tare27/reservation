package com.reservation.service;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.entity.CancellationPolicy;
import com.reservation.mapper.Mapper;
import com.reservation.repository.CancellationPolicyRepository;
import com.reservation.repository.CancellationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CancellationPolicyService {
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final CancellationRepository cancellationRepository;

    public CancellationPolicyResponse createCancellationPolicy(CancellationPolicyRequest policy){
        validateCancellationPolicyRequest(policy);

        boolean exists = cancellationPolicyRepository.existsOverlappingRange(policy.getDaysBeforeCheckInFrom(), policy.getDaysBeforeCheckInTo(), null);
        if (exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Overlapping policy date range"
            );
        CancellationPolicy cancellationPolicy = new CancellationPolicy();
        Mapper.mapRequestToEntity(cancellationPolicy , policy);

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public CancellationPolicyResponse updateCancellationPolicy(Integer id , CancellationPolicyRequest policy){
        validateCancellationPolicyRequest(policy);

        CancellationPolicy cancellationPolicy = cancellationPolicyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cancellation policy with id " + id + " not found"));

        boolean exists = cancellationPolicyRepository.existsOverlappingRange(policy.getDaysBeforeCheckInFrom(), policy.getDaysBeforeCheckInTo(), id);
        if (exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Overlapping policy date range"
            );

        Mapper.mapRequestToEntity(cancellationPolicy , policy);

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public void deleteCancellationPolicy(Integer id){
        boolean isInUse = cancellationRepository.existsByCancellationPolicy_Id(id);

        if (isInUse)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Policy is in use and cannot be deleted"
            );

        cancellationPolicyRepository.deleteById(id);
    }

    public List<CancellationPolicyResponse> getCancellationPolicies(){
        return cancellationPolicyRepository.findAll()
                .stream()
                .map(CancellationPolicyResponse::new)
                .toList();
    }

    public CancellationPolicyResponse getCancellationPolicyById(Integer id){
        CancellationPolicy policy = cancellationPolicyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No policy found for this id"
                ));
        return new CancellationPolicyResponse(policy);
    }

    //Helper Methods

    private void validateCancellationPolicyRequest(CancellationPolicyRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancellation policy request is required");
        }

        if (request.getDaysBeforeCheckInFrom() >= request.getDaysBeforeCheckInTo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'Days before check-in from' must be less than 'to' value");
        }

        if (request.getDaysBeforeCheckInFrom() < 0 || request.getDaysBeforeCheckInTo() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Days before check-in cannot be negative");
        }
    }
}
