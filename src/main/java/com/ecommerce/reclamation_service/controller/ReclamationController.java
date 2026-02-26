package com.ecommerce.reclamation_service.controller;

import com.ecommerce.reclamation_service.dto.CreateReclamationRequest;
import com.ecommerce.reclamation_service.dto.ReclamationDTO;
import com.ecommerce.reclamation_service.service.ReclamationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reclamations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReclamationController {
    private final ReclamationService reclamationService;

    // ── CREATE ──────────────────────────────────────────────────────────────
    
    @PostMapping
    public ResponseEntity<ReclamationDTO> create(@RequestBody CreateReclamationRequest request) {
        return ResponseEntity.ok(reclamationService.createReclamation(request));
    }

    // ── READ ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ReclamationDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.getReclamation(id));
    }

    @GetMapping
    public ResponseEntity<List<ReclamationDTO>> getAll() {
        return ResponseEntity.ok(reclamationService.getAllReclamations());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReclamationDTO>> getByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(reclamationService.getMemberReclamations(memberId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReclamationDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reclamationService.getByStatus(status));
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ReclamationDTO> resolve(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String resolutionNotes = body.get("resolutionNotes");
        return ResponseEntity.ok(reclamationService.resolveReclamation(id, resolutionNotes));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ReclamationDTO> reject(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        return ResponseEntity.ok(reclamationService.rejectReclamation(id, reason));
    }

    // ── COMMENTS & ATTACHMENTS ─────────────────────────────────────────────

    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long memberId = Long.valueOf(body.get("memberId").toString());
        String commentText = body.get("commentText").toString();
        reclamationService.addComment(id, memberId, commentText);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<Void> attachFile(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String fileUrl = body.get("fileUrl");
        String fileName = body.get("fileName");
        reclamationService.attachFile(id, fileUrl, fileName);
        return ResponseEntity.ok().build();
    }

    // ── DELETE ──────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reclamationService.deleteReclamation(id);
        return ResponseEntity.noContent().build();
    }
}
