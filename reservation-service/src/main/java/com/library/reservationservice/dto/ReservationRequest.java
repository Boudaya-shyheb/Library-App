package com.library.reservationservice.dto;

import com.library.reservationservice.entity.ReservationStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {

    @NotNull(message = "Space ID is required")
    private Long spaceId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Size(max = 200)
    private String userFullName;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @Size(max = 500)
    private String purpose;
}
