package com.esprit.microservice.emprunt.client;

import com.esprit.microservice.emprunt.DTO.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// The @FeignClient name is the Eureka name of the microservice we want to contact.
@FeignClient(name = "inventaire-service")
public interface InventaireClient {

    @GetMapping("/api/inventaire/books/{id}")
   BookDTO.ApiResponse<BookDTO> getBookById(@PathVariable("id") Long id);

}
