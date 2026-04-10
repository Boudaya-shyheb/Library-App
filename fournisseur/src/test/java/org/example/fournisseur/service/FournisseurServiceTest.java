package org.example.fournisseur.service;

import org.example.fournisseur.exception.ResourceNotFoundException;
import org.example.fournisseur.repository.FournisseurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FournisseurServiceTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @InjectMocks
    private FournisseurService fournisseurService;

    @Test
    void deleteById_throwsWhenMissing() {
        when(fournisseurRepository.existsById(7L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> fournisseurService.deleteById(7L));
    }
}
