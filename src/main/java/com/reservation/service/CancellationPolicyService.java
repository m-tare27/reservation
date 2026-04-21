package com.reservation.service;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.entity.CancellationPolicy;
import com.reservation.mapper.Mapper;
import com.reservation.repository.CancellationPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancellationPolicyService {
    private final CancellationPolicyRepository cancellationPolicyRepository;

    public CancellationPolicyResponse createCancellationPolicy(CancellationPolicyRequest policy){
        CancellationPolicy cancellationPolicy = new CancellationPolicy();

        Mapper.mapRequestToEntity(cancellationPolicy , policy);

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public CancellationPolicyResponse updateCancellationPolicy(Integer id , CancellationPolicyRequest policy){
        CancellationPolicy cancellationPolicy = cancellationPolicyRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Invalid Policy"));

        Mapper.mapRequestToEntity(cancellationPolicy , policy);

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public void deleteCancellationPolicy(Integer id){
        cancellationPolicyRepository.deleteById(id);
    }

}
