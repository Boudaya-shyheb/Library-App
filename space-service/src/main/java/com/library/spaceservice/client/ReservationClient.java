package com.library.spaceservice.client;

import com.library.spaceservice.dto.ReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * OpenFeign declarative HTTP client for communicating with reservation-service.
 *
 * <p>The {@code name} attribute matches the {@code spring.application.name} of the
 * target service as registered in Eureka — Spring Cloud LoadBalancer will resolve
 * the actual host/port automatically.</p>
 *
 * <p>A {@code fallback} or {@code fallbackFactory} can be added here to provide
 * circuit-breaker behaviour (requires Resilience4j on the classpath).</p>
 */
@FeignClient(
    name = "reservation-service",               // Eureka service ID
    path = "/api/reservations"                  // Base path of the remote REST resource
)
public interface ReservationClient {

    /**
     * Retrieve all reservations associated with a given space.
     *
     * @param spaceId the ID of the space
     * @return list of reservations for the space
     */
    @GetMapping("/space/{spaceId}")
    List<ReservationResponse> getReservationsBySpaceId(@PathVariable("spaceId") Long spaceId);

    /**
     * Check if a space currently has any active (confirmed) reservation.
     *
     * @param spaceId the ID of the space
     * @return true if the space has active reservations
     */
    @GetMapping("/space/{spaceId}/active")
    Boolean hasActiveReservation(@PathVariable("spaceId") Long spaceId);

    /**
     * Cancel all reservations for a space (called when a space is put under maintenance).
     *
     * @param spaceId the ID of the space
     * @param reason  reason for cancellation
     */
    @PostMapping("/space/{spaceId}/cancel-all")
    void cancelAllReservationsForSpace(
        @PathVariable("spaceId") Long spaceId,
        @RequestParam("reason") String reason
    );
}
