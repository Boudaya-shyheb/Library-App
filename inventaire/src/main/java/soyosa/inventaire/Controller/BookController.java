package soyosa.inventaire.Controller;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soyosa.inventaire.Model.Book;
import soyosa.inventaire.Service.BookService;
import soyosa.inventaire.api.ApiResponse;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/inventaire/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Book>> addBook(@RequestBody Book book) {
        Book created = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Book created", Map.of()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Book>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookId") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String q
    ) {
        Page<Book> result = bookService.getBooks(page, size, sort, dir, q);
        Map<String, Object> meta = Map.of(
                "page", page,
                "size", size,
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        );
        return ResponseEntity.ok(ApiResponse.ok(result, "Books fetched", meta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookService.getBookById(id), "Book fetched", Map.of()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable Long id,
                                                         @RequestBody Book book) {
        return ResponseEntity.ok(ApiResponse.ok(bookService.updateBook(id, book), "Book updated", Map.of()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Book deleted", Map.of()));
    }


}
