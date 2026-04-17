package com.esprit.microservice.emprunt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    public static class ApiResponse<T> {
        private String message;
        private Map<String, Object> meta;
        private T data;
        
        public T getData() {
            return data;
        }
    }

    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int publicationYear;
    private int quantity;
}
