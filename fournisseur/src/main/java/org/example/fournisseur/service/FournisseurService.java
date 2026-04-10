package org.example.fournisseur.service;

import java.util.List;
import java.util.Optional;
import org.example.fournisseur.entity.Fournisseur;
import org.example.fournisseur.exception.ResourceNotFoundException;
import org.example.fournisseur.repository.FournisseurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public FournisseurService(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }

    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll();
    }

    public Page<Fournisseur> findPage(int page, int size, String sortBy, String direction, String q) {
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (q == null || q.isBlank()) {
            return fournisseurRepository.findAll(pageable);
        }
        return fournisseurRepository.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
    }

    public Optional<Fournisseur> findById(Long id) {
        return fournisseurRepository.findById(id);
    }

    public Fournisseur create(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    public Optional<Fournisseur> update(Long id, Fournisseur fournisseur) {
        if (!fournisseurRepository.existsById(id)) {
            return Optional.empty();
        }
        fournisseur.setId(id);
        return Optional.of(fournisseurRepository.save(fournisseur));
    }

    public boolean deleteById(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fournisseur " + id + " not found");
        }
        fournisseurRepository.deleteById(id);
        return true;
    }
}
