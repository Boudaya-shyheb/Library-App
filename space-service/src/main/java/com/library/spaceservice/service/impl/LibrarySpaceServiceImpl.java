package com.library.spaceservice.service.impl;

import com.library.spaceservice.client.ReservationClient;
import com.library.spaceservice.dto.LibrarySpaceRequest;
import com.library.spaceservice.dto.LibrarySpaceResponse;
import com.library.spaceservice.dto.ReservationResponse;
import com.library.spaceservice.entity.LibrarySpace;
import com.library.spaceservice.entity.SpaceAvailability;
import com.library.spaceservice.entity.SpaceType;
import com.library.spaceservice.exception.BusinessException;
import com.library.spaceservice.exception.ResourceNotFoundException;
import com.library.spaceservice.mapper.LibrarySpaceMapper;
import com.library.spaceservice.repository.LibrarySpaceRepository;
import com.library.spaceservice.service.LibrarySpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LibrarySpaceServiceImpl implements LibrarySpaceService {

    private final LibrarySpaceRepository spaceRepository;
    private final LibrarySpaceMapper mapper;
    private final ReservationClient reservationClient;

    // ── READ ─────────────────────────────────────────────────────────────────

    @Override
    public List<LibrarySpaceResponse> getAllSpaces() {
        log.debug("Fetching all active library spaces");
        return spaceRepository.findByActiveTrue()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LibrarySpaceResponse getSpaceById(Long id) {
        log.debug("Fetching library space with id={}", id);
        LibrarySpace space = spaceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("LibrarySpace", "id", id));
        return mapper.toResponse(space);
    }

    @Override
    public List<LibrarySpaceResponse> getSpacesByStatus(SpaceAvailability status) {
        log.debug("Fetching spaces with status={}", status);
        return spaceRepository.findByAvailabilityStatusAndActiveTrue(status)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LibrarySpaceResponse> getSpacesByType(SpaceType type) {
        log.debug("Fetching spaces with type={}", type);
        return spaceRepository.findByTypeAndActiveTrue(type)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LibrarySpaceResponse> searchSpacesByName(String keyword) {
        log.debug("Searching spaces with keyword='{}'", keyword);
        return spaceRepository.searchByName(keyword)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LibrarySpaceResponse> getSpacesByMinCapacity(int minCapacity) {
        log.debug("Fetching spaces with capacity >= {}", minCapacity);
        return spaceRepository.findByMinCapacity(minCapacity)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LibrarySpaceResponse createSpace(LibrarySpaceRequest request) {
        log.info("Creating new library space: name='{}'", request.getName());

        if (spaceRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new BusinessException(
                "A space with name '" + request.getName() + "' already exists"
            );
        }

        LibrarySpace space = mapper.toEntity(request);
        LibrarySpace saved = spaceRepository.save(space);

        log.info("Library space created with id={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LibrarySpaceResponse updateSpace(Long id, LibrarySpaceRequest request) {
        log.info("Updating library space id={}", id);

        LibrarySpace space = spaceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("LibrarySpace", "id", id));

        // Allow same name if it belongs to the same space
        if (!space.getName().equals(request.getName()) &&
                spaceRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new BusinessException(
                "A space with name '" + request.getName() + "' already exists"
            );
        }

        mapper.updateEntityFromRequest(request, space);
        LibrarySpace updated = spaceRepository.save(space);

        log.info("Library space id={} updated successfully", id);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public LibrarySpaceResponse updateAvailabilityStatus(Long id, SpaceAvailability status) {
        log.info("Updating availability of space id={} to {}", id, status);

        LibrarySpace space = spaceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("LibrarySpace", "id", id));

        // Business rule: cannot set AVAILABLE if there is an active reservation
        if (status == SpaceAvailability.MAINTENANCE) {
            try {
                reservationClient.cancelAllReservationsForSpace(id, "Space under maintenance");
                log.info("Cancelled all reservations for space id={} due to maintenance", id);
            } catch (Exception e) {
                // Log but do not fail — reservation service may be temporarily down
                log.warn("Could not cancel reservations for space id={}: {}", id, e.getMessage());
            }
        }

        space.setAvailabilityStatus(status);
        LibrarySpace updated = spaceRepository.save(space);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSpace(Long id) {
        log.info("Soft-deleting library space id={}", id);

        LibrarySpace space = spaceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("LibrarySpace", "id", id));

        space.setActive(false);
        space.setAvailabilityStatus(SpaceAvailability.CLOSED);
        spaceRepository.save(space);

        log.info("Library space id={} soft-deleted", id);
    }

    // ── FEIGN ────────────────────────────────────────────────────────────────

    @Override
    public List<ReservationResponse> getReservationsForSpace(Long spaceId) {
        log.debug("Fetching reservations for space id={} via Feign", spaceId);

        // Verify space exists before making inter-service call
        if (!spaceRepository.findByIdAndActiveTrue(spaceId).isPresent()) {
            throw new ResourceNotFoundException("LibrarySpace", "id", spaceId);
        }

        try {
            List<ReservationResponse> reservations =
                reservationClient.getReservationsBySpaceId(spaceId);
            log.debug("Retrieved {} reservations for space id={}", reservations.size(), spaceId);
            return reservations;
        } catch (Exception e) {
            log.error("Failed to fetch reservations for space id={}: {}", spaceId, e.getMessage());
            // Return empty list as a graceful degradation (use Resilience4j fallback in prod)
            return Collections.emptyList();
        }
    }
}
