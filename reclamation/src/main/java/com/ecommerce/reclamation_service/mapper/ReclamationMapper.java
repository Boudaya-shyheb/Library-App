package com.ecommerce.reclamation_service.mapper;

import com.ecommerce.reclamation_service.dto.*;
import com.ecommerce.reclamation_service.entity.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReclamationMapper {
    public static ReclamationDTO toDTO(Reclamation entity) {
        if (entity == null) return null;
        return ReclamationDTO.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .type(entity.getType())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .resolutionNotes(entity.getResolutionNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .comments(toCommentDTOList(entity.getComments()))
                .attachments(toAttachmentDTOList(entity.getAttachments()))
                .build();
    }

    public static List<ReclamationCommentDTO> toCommentDTOList(List<ReclamationComment> comments) {
        if (comments == null) return null;
        return comments.stream().map(ReclamationMapper::toCommentDTO).collect(Collectors.toList());
    }

    public static ReclamationCommentDTO toCommentDTO(ReclamationComment comment) {
        if (comment == null) return null;
        return ReclamationCommentDTO.builder()
                .id(comment.getId())
                .memberId(comment.getMemberId())
                .commentText(comment.getCommentText())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static List<ReclamationAttachmentDTO> toAttachmentDTOList(List<ReclamationAttachment> attachments) {
        if (attachments == null) return null;
        return attachments.stream().map(ReclamationMapper::toAttachmentDTO).collect(Collectors.toList());
    }

    public static ReclamationAttachmentDTO toAttachmentDTO(ReclamationAttachment attachment) {
        if (attachment == null) return null;
        return ReclamationAttachmentDTO.builder()
                .id(attachment.getId())
                .fileUrl(attachment.getFileUrl())
                .fileName(attachment.getFileName())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}
