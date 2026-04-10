package com.ecommerce.reclamation_service.repository;

import com.ecommerce.reclamation_service.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByMemberId(Long memberId);
    List<Reclamation> findByStatus(String status);
}
