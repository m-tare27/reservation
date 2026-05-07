package com.reservation.service;

import com.reservation.repository.CommissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;

    public void calculateCommission() {

    }
}
