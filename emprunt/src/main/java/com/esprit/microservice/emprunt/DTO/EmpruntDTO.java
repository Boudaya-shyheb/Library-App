package com.esprit.microservice.emprunt.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpruntDTO {
    private Long id;
    private Long userId;
    private Long documentId;
    private String documentType;
    private LocalDateTime empruntDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String statut;
    private String notes;
}