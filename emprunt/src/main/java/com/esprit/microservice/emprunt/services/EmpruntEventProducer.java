package com.esprit.microservice.emprunt.services;

import com.esprit.microservice.emprunt.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpruntEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendDocumentBorrowedEvent(Long documentId) {
        log.info("Publishing DOCUMENT_BORROWED event for document {}", documentId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("documentId", documentId);
        payload.put("action", "BORROWED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "document.borrowed", payload);
    }

    public void sendDocumentReturnedEvent(Long documentId) {
        log.info("Publishing DOCUMENT_RETURNED event for document {}", documentId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("documentId", documentId);
        payload.put("action", "RETURNED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "document.returned", payload);
    }
}
