package org.example.fournisseur.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.example.fournisseur.api.ApiResponse;
import org.example.fournisseur.entity.Fournisseur;
import org.example.fournisseur.service.FournisseurService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ApiResponse<Page<Fournisseur>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String q
    ) {
        Page<Fournisseur> result = fournisseurService.findPage(page, size, sort, dir, q);
        Map<String, Object> meta = Map.of(
                "page", page,
                "size", size,
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        );
        return ResponseEntity.ok(ApiResponse.ok(result, "Fournisseurs fetched", meta));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un fournisseur", description = "Retourne un fournisseur par identifiant")
    public ResponseEntity<ApiResponse<Fournisseur>> getById(@PathVariable Long id) {
        return fournisseurService.findById(id)
                .map(item -> ResponseEntity.ok(ApiResponse.ok(item, "Fournisseur fetched", Map.of())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un fournisseur", description = "Crée un nouveau fournisseur")
    public ResponseEntity<ApiResponse<Fournisseur>> create(@RequestBody Fournisseur fournisseur) {
        Fournisseur saved = fournisseurService.create(fournisseur);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(saved, "Fournisseur created", Map.of()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un fournisseur", description = "Met à jour un fournisseur existant")
    public ResponseEntity<ApiResponse<Fournisseur>> update(@PathVariable Long id, @RequestBody Fournisseur fournisseur) {
        return fournisseurService.update(id, fournisseur)
                .map(item -> ResponseEntity.ok(ApiResponse.ok(item, "Fournisseur updated", Map.of())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un fournisseur", description = "Supprime un fournisseur par identifiant")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        fournisseurService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Fournisseur deleted", Map.of()));
    }
}
