package com.ecommerce.reclamation_service.repository;

import com.ecommerce.reclamation_service.entity.ReclamationAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamationAttachmentRepository extends JpaRepository<ReclamationAttachment, Long> {
}
