package com.library.spaceservice.dto;

import com.library.spaceservice.entity.SpaceAvailability;
import com.library.spaceservice.entity.SpaceType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for LibrarySpace — never exposes internal entity directly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibrarySpaceResponse {

    private Long id;
    private String name;
    private SpaceType type;
    private Integer capacity;
    private String location;
    private String description;
    private SpaceAvailability availabilityStatus;
    private Boolean active;
    private String equipment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
