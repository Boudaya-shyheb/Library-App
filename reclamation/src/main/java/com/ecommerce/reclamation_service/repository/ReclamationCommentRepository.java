package com.ecommerce.reclamation_service.repository;

import com.ecommerce.reclamation_service.entity.ReclamationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamationCommentRepository extends JpaRepository<ReclamationComment, Long> {
}
