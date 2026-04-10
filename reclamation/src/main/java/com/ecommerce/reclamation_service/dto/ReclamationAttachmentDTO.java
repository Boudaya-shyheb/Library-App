package com.ecommerce.reclamation_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamationAttachmentDTO {
    private Long id;
    private String fileUrl;
    private String fileName;
    private LocalDateTime uploadedAt;
}
