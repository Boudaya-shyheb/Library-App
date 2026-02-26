package com.library.spaceservice.repository;

import com.library.spaceservice.entity.LibrarySpace;
import com.library.spaceservice.entity.SpaceAvailability;
import com.library.spaceservice.entity.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibrarySpaceRepository extends JpaRepository<LibrarySpace, Long> {

    /** Find all active spaces */
    List<LibrarySpace> findByActiveTrue();

    /** Find active space by ID */
    Optional<LibrarySpace> findByIdAndActiveTrue(Long id);

    /** Filter active spaces by availability status */
    List<LibrarySpace> findByAvailabilityStatusAndActiveTrue(SpaceAvailability status);

    /** Filter active spaces by type */
    List<LibrarySpace> findByTypeAndActiveTrue(SpaceType type);

    /** Search by name (case-insensitive, partial match) */
    @Query("SELECT s FROM LibrarySpace s WHERE s.active = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<LibrarySpace> searchByName(@Param("keyword") String keyword);

    /** Find spaces with sufficient capacity */
    @Query("SELECT s FROM LibrarySpace s WHERE s.active = true AND s.capacity >= :minCapacity")
    List<LibrarySpace> findByMinCapacity(@Param("minCapacity") int minCapacity);

    /** Check whether a space name already exists */
    boolean existsByNameAndActiveTrue(String name);
}
