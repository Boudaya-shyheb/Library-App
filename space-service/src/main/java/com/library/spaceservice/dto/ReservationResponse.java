package com.library.spaceservice.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO representing a Reservation returned by the Reservation Service via Feign.
 * Mirrors the ReservationResponse from reservation-service.
 */
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
    private String status;        // e.g. CONFIRMED, PENDING, CANCELLED
    private String purpose;
    private LocalDateTime createdAt;
}
