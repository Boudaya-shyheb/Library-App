package com.ecommerce.reclamation_service.service;

import com.ecommerce.reclamation_service.dto.CreateReclamationRequest;
import com.ecommerce.reclamation_service.dto.ReclamationDTO;
import com.ecommerce.reclamation_service.entity.Reclamation;
import com.ecommerce.reclamation_service.entity.ReclamationComment;
import com.ecommerce.reclamation_service.entity.ReclamationAttachment;
import com.ecommerce.reclamation_service.exception.ResourceNotFoundException;
import com.ecommerce.reclamation_service.mapper.ReclamationMapper;
import com.ecommerce.reclamation_service.repository.ReclamationRepository;
import com.ecommerce.reclamation_service.repository.ReclamationCommentRepository;
import com.ecommerce.reclamation_service.repository.ReclamationAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReclamationService {
    private final ReclamationRepository reclamationRepository;
    private final ReclamationCommentRepository commentRepository;
    private final ReclamationAttachmentRepository attachmentRepository;

    // ── READ ────────────────────────────────────────────────────────────────

    public ReclamationDTO getReclamation(Long id) {
        return reclamationRepository.findById(id)
                .map(ReclamationMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found"));
    }

    public List<ReclamationDTO> getAllReclamations() {
        return reclamationRepository.findAll().stream()
                .map(ReclamationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReclamationDTO> getMemberReclamations(Long memberId) {
        log.debug("Fetching reclamations for memberId={}", memberId);
        return reclamationRepository.findByMemberId(memberId).stream()
                .map(ReclamationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReclamationDTO> getByStatus(String status) {
        log.debug("Fetching reclamations with status={}", status);
        return reclamationRepository.findByStatus(status).stream()
                .map(ReclamationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── WRITE ────────────────────────────────────────────────────────────────

    @Transactional
    public ReclamationDTO createReclamation(CreateReclamationRequest request) {
        log.info("Creating reclamation for memberId={}, type={}", request.getMemberId(), request.getType());
        Reclamation reclamation = Reclamation.builder()
                .memberId(request.getMemberId())
                .type(request.getType())
                .description(request.getDescription())
                .status("OPEN")
                .build();
        return ReclamationMapper.toDTO(reclamationRepository.save(reclamation));
    }

    @Transactional
    public ReclamationDTO resolveReclamation(Long id, String resolutionNotes) {
        log.info("Resolving reclamation with id={}", id);
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found"));
        reclamation.setStatus("RESOLVED");
        reclamation.setResolutionNotes(resolutionNotes);
        return ReclamationMapper.toDTO(reclamationRepository.save(reclamation));
    }

    @Transactional
    public ReclamationDTO rejectReclamation(Long id, String reason) {
        log.info("Rejecting reclamation with id={}", id);
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found"));
        reclamation.setStatus("REJECTED");
        reclamation.setResolutionNotes(reason);
        return ReclamationMapper.toDTO(reclamationRepository.save(reclamation));
    }

    @Transactional
    public void deleteReclamation(Long id) {
        log.info("Deleting reclamation with id={}", id);
        if (!reclamationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reclamation not found");
        }
        reclamationRepository.deleteById(id);
    }

    // ── COMMENTS & ATTACHMENTS ──────────────────────────────────────────────

    @Transactional
    public void addComment(Long reclamationId, Long memberId, String commentText) {
        log.debug("Adding comment to reclamation={}", reclamationId);
        Reclamation reclamation = reclamationRepository.findById(reclamationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found"));
        
        ReclamationComment comment = ReclamationComment.builder()
                .reclamation(reclamation)
                .memberId(memberId)
                .commentText(commentText)
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void attachFile(Long reclamationId, String fileUrl, String fileName) {
        log.debug("Attaching file to reclamation={}", reclamationId);
        Reclamation reclamation = reclamationRepository.findById(reclamationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found"));
        
        ReclamationAttachment attachment = ReclamationAttachment.builder()
                .reclamation(reclamation)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .build();
        attachmentRepository.save(attachment);
    }
}
