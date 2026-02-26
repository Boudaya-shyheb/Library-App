package org.example.fournisseur.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.example.fournisseur.entity.Fournisseur;
import org.example.fournisseur.service.FournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Fournisseurs", description = "Gestion des fournisseurs")
@RequestMapping("/api/fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @GetMapping
    @Operation(summary = "Lister les fournisseurs", description = "Retourne la liste de tous les fournisseurs")
    public List<Fournisseur> getAll() {
        return fournisseurService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un fournisseur", description = "Retourne un fournisseur par identifiant")
    public ResponseEntity<Fournisseur> getById(@PathVariable Long id) {
        return fournisseurService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un fournisseur", description = "Crée un nouveau fournisseur")
    public ResponseEntity<Fournisseur> create(@RequestBody Fournisseur fournisseur) {
        Fournisseur saved = fournisseurService.create(fournisseur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un fournisseur", description = "Met à jour un fournisseur existant")
    public ResponseEntity<Fournisseur> update(@PathVariable Long id, @RequestBody Fournisseur fournisseur) {
        return fournisseurService.update(id, fournisseur)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un fournisseur", description = "Supprime un fournisseur par identifiant")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!fournisseurService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
