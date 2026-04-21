package com.reservation.dto;

import lombok.Data;

@Data
public class CancellationRequest {
    Integer id;
    String reason;
}
