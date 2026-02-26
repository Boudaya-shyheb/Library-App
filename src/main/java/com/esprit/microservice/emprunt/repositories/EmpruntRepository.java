package com.esprit.microservice.emprunt.repositories;


import com.esprit.microservice.emprunt.entities.Emprunt;
import com.esprit.microservice.emprunt.entities.StatutEmprunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByUserId(Long userId);
    List<Emprunt> findByDocumentId(Long documentId);
    List<Emprunt> findByStatut(StatutEmprunt statut);
    List<Emprunt> findByUserIdAndStatut(Long userId, StatutEmprunt statut);

    @Query("SELECT e FROM Emprunt e WHERE e.dueDate < :currentDate AND e.statut = 'EN_COURS'")
    List<Emprunt> findOverdueEmprunts(@Param("currentDate") LocalDateTime currentDate);

    boolean existsByDocumentIdAndStatut(Long documentId, StatutEmprunt statut);
}