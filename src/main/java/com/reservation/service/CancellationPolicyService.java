package com.reservation.service;

import com.reservation.dto.CancellationPolicyRequest;
import com.reservation.dto.CancellationPolicyResponse;
import com.reservation.entity.CancellationPolicy;
import com.reservation.repository.CancellationPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancellationPolicyService {
    private final CancellationPolicyRepository cancellationPolicyRepository;

    public CancellationPolicyResponse createCancellationPolicy(CancellationPolicyRequest policy){
        CancellationPolicy cancellationPolicy = new CancellationPolicy();

        cancellationPolicy.setName(policy.getName());
        cancellationPolicy.setDaysBeforeCheckInFrom(policy.getDaysBeforeCheckInFrom());
        cancellationPolicy.setDaysBeforeCheckInTo(policy.getDaysBeforeCheckInTo());
        cancellationPolicy.setRefundPercentage(policy.getRefundPercentage());

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public CancellationPolicyResponse updateCancellationPolicy(Integer id , CancellationPolicyRequest policy){
        CancellationPolicy cancellationPolicy = cancellationPolicyRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Invalid Policy"));

        //check for correct range missing

        cancellationPolicy.setName(policy.getName());
        cancellationPolicy.setDaysBeforeCheckInFrom(policy.getDaysBeforeCheckInFrom());
        cancellationPolicy.setDaysBeforeCheckInTo(policy.getDaysBeforeCheckInTo());
        cancellationPolicy.setRefundPercentage(policy.getRefundPercentage());

        CancellationPolicy savedPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return new CancellationPolicyResponse(savedPolicy);
    }

    public void deleteCancellationPolicy(Integer id){
        cancellationPolicyRepository.deleteById(id);
    }

}
