package com.esprit.microservice.emprunt.services;


import com.esprit.microservice.emprunt.DTO.EmpruntDTO;
import com.esprit.microservice.emprunt.entities.Emprunt;
import com.esprit.microservice.emprunt.entities.StatutEmprunt;
import com.esprit.microservice.emprunt.repositories.EmpruntRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpruntService {

    private final EmpruntRepository empruntRepository;

    public EmpruntDTO createEmprunt(EmpruntDTO empruntDTO) {
        Emprunt emprunt = new Emprunt();
        emprunt.setUserId(empruntDTO.getUserId());
        emprunt.setDocumentId(empruntDTO.getDocumentId());
        emprunt.setDocumentType(empruntDTO.getDocumentType());
        emprunt.setEmpruntDate(LocalDateTime.now());
        emprunt.setDueDate(empruntDTO.getDueDate());
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        emprunt.setNotes(empruntDTO.getNotes());

        Emprunt savedEmprunt = empruntRepository.save(emprunt);
        return convertToDTO(savedEmprunt);
    }

    public List<EmpruntDTO> getAllEmprunts() {
        return empruntRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmpruntDTO getEmpruntById(Long id) {
        Optional<Emprunt> emprunt = empruntRepository.findById(id);
        return emprunt.map(this::convertToDTO).orElse(null);
    }

    public List<EmpruntDTO> getEmpruntsByUser(Long userId) {
        return empruntRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpruntDTO> getEmpruntsByDocument(Long documentId) {
        return empruntRepository.findByDocumentId(documentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmpruntDTO> getEmpruntsByStatus(String status) {
        try {
            StatutEmprunt statutEnum = StatutEmprunt.valueOf(status);
            return empruntRepository.findByStatut(statutEnum)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public EmpruntDTO updateEmprunt(Long id, EmpruntDTO empruntDTO) {
        Optional<Emprunt> optionalEmprunt = empruntRepository.findById(id);
        if (optionalEmprunt.isPresent()) {
            Emprunt emprunt = optionalEmprunt.get();
            if (empruntDTO.getDueDate() != null) {
                emprunt.setDueDate(empruntDTO.getDueDate());
            }
            if (empruntDTO.getNotes() != null) {
                emprunt.setNotes(empruntDTO.getNotes());
            }
            if (empruntDTO.getStatut() != null) {
                try {
                    emprunt.setStatut(StatutEmprunt.valueOf(empruntDTO.getStatut()));
                } catch (IllegalArgumentException e) {
                    // ignore invalid status
                }
            }
            if (empruntDTO.getReturnDate() != null) {
                emprunt.setReturnDate(empruntDTO.getReturnDate());
            }

            Emprunt updatedEmprunt = empruntRepository.save(emprunt);
            return convertToDTO(updatedEmprunt);
        }
        return null;
    }

    public EmpruntDTO returnDocument(Long id) {
        Optional<Emprunt> optionalEmprunt = empruntRepository.findById(id);
        if (optionalEmprunt.isPresent()) {
            Emprunt emprunt = optionalEmprunt.get();
            emprunt.setReturnDate(LocalDateTime.now());
            emprunt.setStatut(StatutEmprunt.RETOURNE);
            Emprunt returnedEmprunt = empruntRepository.save(emprunt);
            return convertToDTO(returnedEmprunt);
        }
        return null;
    }

    public void deleteEmprunt(Long id) {
        empruntRepository.deleteById(id);
    }

    public List<EmpruntDTO> getOverdueEmprunts() {
        return empruntRepository.findOverdueEmprunts(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean isDocumentEmprunte(Long documentId) {
        return empruntRepository.existsByDocumentIdAndStatut(documentId, StatutEmprunt.EN_COURS);
    }

    private EmpruntDTO convertToDTO(Emprunt emprunt) {
        EmpruntDTO dto = new EmpruntDTO();
        dto.setId(emprunt.getId());
        dto.setUserId(emprunt.getUserId());
        dto.setDocumentId(emprunt.getDocumentId());
        dto.setDocumentType(emprunt.getDocumentType());
        dto.setEmpruntDate(emprunt.getEmpruntDate());
        dto.setDueDate(emprunt.getDueDate());
        dto.setReturnDate(emprunt.getReturnDate());
        dto.setStatut(emprunt.getStatut().toString());
        dto.setNotes(emprunt.getNotes());
        return dto;
    }
}