package com.ecommerce.reclamation_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReclamationRequest {
    private Long memberId;
    private String type;
    private String description;
}
