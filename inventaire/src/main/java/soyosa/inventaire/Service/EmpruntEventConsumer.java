package soyosa.inventaire.Service;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import soyosa.inventaire.Config.RabbitMQConfig;
import soyosa.inventaire.Model.Book;
import soyosa.inventaire.Repo.BookRepo;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmpruntEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmpruntEventConsumer.class);

    private final BookRepo bookRepo;

    public EmpruntEventConsumer(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Map<String, Object> payload) {
        log.info("Received message from RabbitMQ: {}", payload);
        
        try {
            Long documentId = ((Number) payload.get("documentId")).longValue();
            String action = (String) payload.get("action");

            Optional<Book> bookOpt = bookRepo.findById(documentId);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                if ("BORROWED".equals(action)) {
                    int newQuantity = book.getQuantity() - 1;
                    book.setQuantity(Math.max(0, newQuantity));
                    bookRepo.save(book);
                    log.info("Decremented stock for book {}. New quantity: {}", documentId, book.getQuantity());
                } else if ("RETURNED".equals(action)) {
                    book.setQuantity(book.getQuantity() + 1);
                    bookRepo.save(book);
                    log.info("Incremented stock for book {}. New quantity: {}", documentId, book.getQuantity());
                }
            } else {
                log.warn("Document {} not found in inventory. Cannot process action: {}", documentId, action);
            }
        } catch (Exception e) {
            log.error("Error processing document event from RabbitMQ", e);
        }
    }
}
