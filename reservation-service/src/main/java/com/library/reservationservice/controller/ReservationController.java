package com.library.reservationservice.controller;

import com.library.reservationservice.dto.ReservationRequest;
import com.library.reservationservice.dto.ReservationResponse;
import com.library.reservationservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Reservation management.
 * Base path: /api/reservations
 *
 * <p>The three endpoints annotated with "Called by Space Service via Feign"
 * are consumed by space-service through the ReservationClient interface.</p>
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // ── READ ─────────────────────────────────────────────────────────────────

    /** GET /api/reservations */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    /** GET /api/reservations/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    /**
     * GET /api/reservations/space/{spaceId}
     * Called by Space Service via Feign (ReservationClient#getReservationsBySpaceId).
     */
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<ReservationResponse>> getBySpaceId(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reservationService.getBySpaceId(spaceId));
    }

    /**
     * GET /api/reservations/space/{spaceId}/active
     * Called by Space Service via Feign (ReservationClient#hasActiveReservation).
     */
    @GetMapping("/space/{spaceId}/active")
    public ResponseEntity<Boolean> hasActiveReservation(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reservationService.hasActiveReservation(spaceId));
    }

    /** GET /api/reservations/user/{userId} */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(reservationService.getByUserId(userId));
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    /** POST /api/reservations */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request));
    }

    /** PATCH /api/reservations/{id}/confirm */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    /** PATCH /api/reservations/{id}/cancel */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancel(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Cancelled by user") String reason) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, reason));
    }

    /**
     * POST /api/reservations/space/{spaceId}/cancel-all
     * Called by Space Service via Feign (ReservationClient#cancelAllReservationsForSpace).
     */
    @PostMapping("/space/{spaceId}/cancel-all")
    public ResponseEntity<Void> cancelAllForSpace(
            @PathVariable Long spaceId,
            @RequestParam String reason) {
        reservationService.cancelAllForSpace(spaceId, reason);
        return ResponseEntity.ok().build();
    }
}
