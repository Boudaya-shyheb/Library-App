package com.library.reservationservice.service;

import com.library.reservationservice.dto.ReservationRequest;
import com.library.reservationservice.dto.ReservationResponse;
import com.library.reservationservice.entity.Reservation;
import com.library.reservationservice.entity.ReservationStatus;
import com.library.reservationservice.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // ── READ ────────────────────────────────────────────────────────────────

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ReservationResponse getById(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));
        return toResponse(r);
    }

    /** Called by Space Service via Feign: GET /api/reservations/space/{spaceId} */
    public List<ReservationResponse> getBySpaceId(Long spaceId) {
        log.debug("Fetching reservations for spaceId={}", spaceId);
        return reservationRepository.findBySpaceId(spaceId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Called by Space Service via Feign: GET /api/reservations/space/{spaceId}/active */
    public Boolean hasActiveReservation(Long spaceId) {
        return reservationRepository.existsBySpaceIdAndStatus(spaceId, ReservationStatus.CONFIRMED);
    }

    public List<ReservationResponse> getByUserId(String userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creating reservation for spaceId={}, userId={}", request.getSpaceId(), request.getUserId());
        Reservation reservation = Reservation.builder()
                .spaceId(request.getSpaceId())
                .userId(request.getUserId())
                .userFullName(request.getUserFullName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .status(ReservationStatus.PENDING)
                .build();
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found: " + id));
        r.setStatus(ReservationStatus.CONFIRMED);
        return toResponse(reservationRepository.save(r));
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id, String reason) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found: " + id));
        r.setStatus(ReservationStatus.CANCELLED);
        return toResponse(reservationRepository.save(r));
    }

    /** Called by Space Service via Feign: POST /api/reservations/space/{spaceId}/cancel-all */
    @Transactional
    public void cancelAllForSpace(Long spaceId, String reason) {
        log.info("Cancelling all reservations for spaceId={}, reason='{}'", spaceId, reason);
        List<Reservation> active = reservationRepository.findBySpaceIdAndStatus(
                spaceId, ReservationStatus.CONFIRMED);
        active.addAll(reservationRepository.findBySpaceIdAndStatus(
                spaceId, ReservationStatus.PENDING));
        active.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
        reservationRepository.saveAll(active);
        log.info("Cancelled {} reservations for spaceId={}", active.size(), spaceId);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────

    private ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .spaceId(r.getSpaceId())
                .userId(r.getUserId())
                .userFullName(r.getUserFullName())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .status(r.getStatus().name())
                .purpose(r.getPurpose())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
