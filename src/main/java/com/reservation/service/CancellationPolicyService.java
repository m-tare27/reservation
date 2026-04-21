package com.reservation.service;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.entity.CancellationPolicy;
import com.reservation.mapper.Mapper;
import com.reservation.repository.CancellationPolicyRepository;
import com.reservation.repository.CancellationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CancellationPolicyService {
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final CancellationRepository cancellationRepository;

    public CancellationPolicyResponse createCancellationPolicy(CancellationPolicyRequest policy){
        CancellationPolicy cancellationPolicy = new CancellationPolicy();

        boolean exists = cancellationPolicyRepository.existsOverlappingRange(policy.getDaysBeforeCheckInFrom(), policy.getDaysBeforeCheckInTo(), null);
        if (exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Overlapping policy date range"
            );

        Mapper.mapRequestToEntity(cancellationPolicy , policy);

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public CancellationPolicyResponse updateCancellationPolicy(Integer id , CancellationPolicyRequest policy){
        CancellationPolicy cancellationPolicy = cancellationPolicyRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Invalid Policy"));

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
        boolean exists = cancellationRepository.existsByCancellationPolicy_Id(id);

        if (exists)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Policy is in use and cannot be deleted"
            );

        cancellationPolicyRepository.deleteById(id);
    }

}
