package com.library.spaceservice.controller;

import com.library.spaceservice.dto.ApiResponse;
import com.library.spaceservice.dto.LibrarySpaceRequest;
import com.library.spaceservice.dto.LibrarySpaceResponse;
import com.library.spaceservice.dto.ReservationResponse;
import com.library.spaceservice.entity.SpaceAvailability;
import com.library.spaceservice.entity.SpaceType;
import com.library.spaceservice.service.LibrarySpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Library Space management.
 * All endpoints are prefixed with /api/spaces.
 */
@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
@Slf4j
public class LibrarySpaceController {

    private final LibrarySpaceService spaceService;

    // ── READ ─────────────────────────────────────────────────────────────────

    /**
     * GET /api/spaces
     * Returns all active library spaces.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LibrarySpaceResponse>>> getAllSpaces() {
        List<LibrarySpaceResponse> spaces = spaceService.getAllSpaces();
        return ResponseEntity.ok(ApiResponse.success("Retrieved " + spaces.size() + " spaces", spaces));
    }

    /**
     * GET /api/spaces/{id}
     * Returns a single space by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibrarySpaceResponse>> getSpaceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(spaceService.getSpaceById(id)));
    }

    /**
     * GET /api/spaces/status/{status}
     * Returns spaces filtered by availability status.
     * Example: GET /api/spaces/status/AVAILABLE
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<LibrarySpaceResponse>>> getSpacesByStatus(
            @PathVariable SpaceAvailability status) {
        return ResponseEntity.ok(ApiResponse.success(spaceService.getSpacesByStatus(status)));
    }

    /**
     * GET /api/spaces/type/{type}
     * Returns spaces filtered by type.
     * Example: GET /api/spaces/type/LAB
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<LibrarySpaceResponse>>> getSpacesByType(
            @PathVariable SpaceType type) {
        return ResponseEntity.ok(ApiResponse.success(spaceService.getSpacesByType(type)));
    }

    /**
     * GET /api/spaces/search?keyword=room
     * Full-text search by space name.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LibrarySpaceResponse>>> searchSpaces(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(spaceService.searchSpacesByName(keyword)));
    }

    /**
     * GET /api/spaces/capacity?min=10
     * Returns spaces with capacity >= min.
     */
    @GetMapping("/capacity")
    public ResponseEntity<ApiResponse<List<LibrarySpaceResponse>>> getSpacesByCapacity(
            @RequestParam(name = "min", defaultValue = "1") int min) {
        return ResponseEntity.ok(ApiResponse.success(spaceService.getSpacesByMinCapacity(min)));
    }

    /**
     * GET /api/spaces/{id}/reservations
     * Fetches reservations for a space via OpenFeign → Reservation Service.
     */
    @GetMapping("/{id}/reservations")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsForSpace(
            @PathVariable Long id) {
        List<ReservationResponse> reservations = spaceService.getReservationsForSpace(id);
        return ResponseEntity.ok(
            ApiResponse.success("Reservations for space id=" + id, reservations)
        );
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    /**
     * POST /api/spaces
     * Creates a new library space.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LibrarySpaceResponse>> createSpace(
            @Valid @RequestBody LibrarySpaceRequest request) {
        LibrarySpaceResponse created = spaceService.createSpace(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(created));
    }

    /**
     * PUT /api/spaces/{id}
     * Fully updates an existing space.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibrarySpaceResponse>> updateSpace(
            @PathVariable Long id,
            @Valid @RequestBody LibrarySpaceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Space updated", spaceService.updateSpace(id, request)));
    }

    /**
     * PATCH /api/spaces/{id}/status?status=OCCUPIED
     * Partially updates only the availability status.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LibrarySpaceResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam SpaceAvailability status) {
        return ResponseEntity.ok(
            ApiResponse.success("Status updated", spaceService.updateAvailabilityStatus(id, status))
        );
    }

    /**
     * DELETE /api/spaces/{id}
     * Soft-deletes a library space (sets active=false).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(@PathVariable Long id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.ok(ApiResponse.success("Space deleted successfully", null));
    }
}
