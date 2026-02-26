package com.library.spaceservice.dto;

import com.library.spaceservice.entity.SpaceAvailability;
import com.library.spaceservice.entity.SpaceType;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for creating or updating a LibrarySpace.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibrarySpaceRequest {

    @NotBlank(message = "Space name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @NotNull(message = "Space type is required")
    private SpaceType type;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 1000, message = "Capacity cannot exceed 1000")
    private Integer capacity;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private SpaceAvailability availabilityStatus;

    @Size(max = 500, message = "Equipment info must not exceed 500 characters")
    private String equipment;
}
