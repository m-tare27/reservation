package com.reservation.dto;

import com.reservation.entity.Cancellation;
import com.reservation.entity.RefundStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CancellationResponse {

    Integer id;

    LocalDateTime cancelledAt;

    long daysBeforeCheckIn;

    double refundAmount;

    RefundStatus refundStatus;

    String reason;

    public CancellationResponse(Cancellation cancellation){
        this.id = cancellation.getId();
        this.cancelledAt = cancellation.getCancelledAt();
        this.daysBeforeCheckIn = cancellation.getDaysBeforeCheckIn();
        this.refundAmount = cancellation.getRefundAmount();
        this.refundStatus = cancellation.getRefundStatus();
        this.reason = cancellation.getReason();
    }

}
