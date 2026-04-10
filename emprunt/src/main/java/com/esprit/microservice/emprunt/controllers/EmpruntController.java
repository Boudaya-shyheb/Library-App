package com.esprit.microservice.emprunt.controllers;


import com.esprit.microservice.emprunt.DTO.EmpruntDTO;
import com.esprit.microservice.emprunt.services.EmpruntService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/emprunts")
@RequiredArgsConstructor
public class EmpruntController {

    private final EmpruntService empruntService;

    @PostMapping
    public ResponseEntity<EmpruntDTO> createEmprunt(@RequestBody EmpruntDTO empruntDTO) {
        EmpruntDTO created = empruntService.createEmprunt(empruntDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmpruntDTO>> getAllEmprunts() {
        return ResponseEntity.ok(empruntService.getAllEmprunts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpruntDTO> getEmpruntById(@PathVariable Long id) {
        EmpruntDTO emprunt = empruntService.getEmpruntById(id);
        if (emprunt != null) {
            return ResponseEntity.ok(emprunt);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmpruntDTO>> getEmpruntsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(empruntService.getEmpruntsByUser(userId));
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<EmpruntDTO>> getEmpruntsByDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(empruntService.getEmpruntsByDocument(documentId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmpruntDTO>> getEmpruntsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(empruntService.getEmpruntsByStatus(status));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<EmpruntDTO>> getOverdueEmprunts() {
        return ResponseEntity.ok(empruntService.getOverdueEmprunts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpruntDTO> updateEmprunt(@PathVariable Long id, @RequestBody EmpruntDTO empruntDTO) {
        EmpruntDTO updated = empruntService.updateEmprunt(id, empruntDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<EmpruntDTO> returnDocument(@PathVariable Long id) {
        EmpruntDTO returned = empruntService.returnDocument(id);
        if (returned != null) {
            return ResponseEntity.ok(returned);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmprunt(@PathVariable Long id) {
        empruntService.deleteEmprunt(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{documentId}")
    public ResponseEntity<Boolean> isDocumentEmprunte(@PathVariable Long documentId) {
        return ResponseEntity.ok(empruntService.isDocumentEmprunte(documentId));
    }
}