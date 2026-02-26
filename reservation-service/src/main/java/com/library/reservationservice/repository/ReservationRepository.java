package com.library.reservationservice.repository;

import com.library.reservationservice.entity.Reservation;
import com.library.reservationservice.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** All reservations for a given space (called by Space Service via Feign) */
    List<Reservation> findBySpaceId(Long spaceId);

    /** Active reservations for a space */
    List<Reservation> findBySpaceIdAndStatus(Long spaceId, ReservationStatus status);

    /** Check if any confirmed reservation exists for a space */
    boolean existsBySpaceIdAndStatus(Long spaceId, ReservationStatus status);

    /** All reservations for a user */
    List<Reservation> findByUserId(String userId);
}
