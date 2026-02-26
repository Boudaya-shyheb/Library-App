package com.ecommerce.reclamation_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamationDTO {
    private Long id;
    private Long memberId;
    private String type;
    private String description;
    private String status;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReclamationCommentDTO> comments;
    private List<ReclamationAttachmentDTO> attachments;
}
