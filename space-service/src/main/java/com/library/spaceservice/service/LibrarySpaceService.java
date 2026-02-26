package com.library.spaceservice.service;

import com.library.spaceservice.dto.LibrarySpaceRequest;
import com.library.spaceservice.dto.LibrarySpaceResponse;
import com.library.spaceservice.dto.ReservationResponse;
import com.library.spaceservice.entity.SpaceAvailability;
import com.library.spaceservice.entity.SpaceType;

import java.util.List;

/**
 * Service interface for Library Space operations.
 * Follows the Interface Segregation Principle — implementation details are hidden.
 */
public interface LibrarySpaceService {

    /** Return all active library spaces */
    List<LibrarySpaceResponse> getAllSpaces();

    /** Return a single active space by ID */
    LibrarySpaceResponse getSpaceById(Long id);

    /** Filter active spaces by availability status */
    List<LibrarySpaceResponse> getSpacesByStatus(SpaceAvailability status);

    /** Filter active spaces by type */
    List<LibrarySpaceResponse> getSpacesByType(SpaceType type);

    /** Full-text name search */
    List<LibrarySpaceResponse> searchSpacesByName(String keyword);

    /** Filter spaces with at least the given capacity */
    List<LibrarySpaceResponse> getSpacesByMinCapacity(int minCapacity);

    /** Create a new library space */
    LibrarySpaceResponse createSpace(LibrarySpaceRequest request);

    /** Fully update an existing space */
    LibrarySpaceResponse updateSpace(Long id, LibrarySpaceRequest request);

    /** Partially update the availability status of a space */
    LibrarySpaceResponse updateAvailabilityStatus(Long id, SpaceAvailability status);

    /** Soft-delete a space (sets active = false) */
    void deleteSpace(Long id);

    /** Fetch all reservations linked to a space via the Reservation Service (OpenFeign) */
    List<ReservationResponse> getReservationsForSpace(Long spaceId);
}
