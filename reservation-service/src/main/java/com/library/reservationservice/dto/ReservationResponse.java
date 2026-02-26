package com.library.reservationservice.dto;

import com.library.reservationservice.entity.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private Long spaceId;
    private String userId;
    private String userFullName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String purpose;
    private LocalDateTime createdAt;
}
