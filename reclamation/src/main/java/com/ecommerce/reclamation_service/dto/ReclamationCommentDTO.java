package com.ecommerce.reclamation_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamationCommentDTO {
    private Long id;
    private Long memberId;
    private String commentText;
    private LocalDateTime createdAt;
}
