package com.reservation.dto;

import com.reservation.entity.CancellationPolicy;
import lombok.Data;

@Data
public class CancellationPolicyResponse {

    private Integer id;

    private String name;

    private int daysBeforeCheckInFrom;

    private int daysBeforeCheckInTo;

    private double refundPercentage;

    public CancellationPolicyResponse(CancellationPolicy policy) {
        this.id = policy.getId();
        this.name = policy.getName();
        this.daysBeforeCheckInFrom = policy.getDaysBeforeCheckInFrom();
        this.daysBeforeCheckInTo = policy.getDaysBeforeCheckInTo();
        this.refundPercentage = policy.getRefundPercentage();
    }
}