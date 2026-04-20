package com.reservation.dto;

import com.reservation.entity.ReservationStatus;
import lombok.Data;

@Data
public class UpdateReservationStatusRequest {
    ReservationStatus status;
}
