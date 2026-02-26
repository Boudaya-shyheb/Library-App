package com.library.spaceservice.mapper;

import com.library.spaceservice.dto.LibrarySpaceRequest;
import com.library.spaceservice.dto.LibrarySpaceResponse;
import com.library.spaceservice.entity.LibrarySpace;
import com.library.spaceservice.entity.SpaceAvailability;
import org.springframework.stereotype.Component;

/**
 * Manual mapper between LibrarySpace entity and DTOs.
 * Avoids adding MapStruct dependency for simplicity.
 */
@Component
public class LibrarySpaceMapper {

    public LibrarySpace toEntity(LibrarySpaceRequest request) {
        return LibrarySpace.builder()
                .name(request.getName())
                .type(request.getType())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .description(request.getDescription())
                .availabilityStatus(
                    request.getAvailabilityStatus() != null
                        ? request.getAvailabilityStatus()
                        : SpaceAvailability.AVAILABLE
                )
                .equipment(request.getEquipment())
                .active(true)
                .build();
    }

    public LibrarySpaceResponse toResponse(LibrarySpace space) {
        return LibrarySpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .type(space.getType())
                .capacity(space.getCapacity())
                .location(space.getLocation())
                .description(space.getDescription())
                .availabilityStatus(space.getAvailabilityStatus())
                .active(space.getActive())
                .equipment(space.getEquipment())
                .createdAt(space.getCreatedAt())
                .updatedAt(space.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequest(LibrarySpaceRequest request, LibrarySpace space) {
        space.setName(request.getName());
        space.setType(request.getType());
        space.setCapacity(request.getCapacity());
        space.setLocation(request.getLocation());
        space.setDescription(request.getDescription());
        space.setEquipment(request.getEquipment());
        if (request.getAvailabilityStatus() != null) {
            space.setAvailabilityStatus(request.getAvailabilityStatus());
        }
    }
}
