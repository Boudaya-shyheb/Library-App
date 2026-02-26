package org.example.fournisseur.service;

import java.util.List;
import java.util.Optional;
import org.example.fournisseur.entity.Fournisseur;
import org.example.fournisseur.repository.FournisseurRepository;
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
            return false;
        }
        fournisseurRepository.deleteById(id);
        return true;
    }
}
