package soyosa.inventaire.Service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import soyosa.inventaire.Model.Book;
import soyosa.inventaire.Repo.BookRepo;
import soyosa.inventaire.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public Book addBook(Book book) {

        book.setQuantity(0); // stock initial
        return bookRepo.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public Page<Book> getBooks(int page, int size, String sortBy, String direction, String q) {
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (q == null || q.isBlank()) {
            return bookRepo.findAll(pageable);
        }
        return bookRepo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q, pageable);
    }

    public Book getBookById(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book " + id + " not found"));
    }

    public Book updateBook(Long id, Book updatedBook) {

        Book book = getBookById(id);

        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setIsbn(updatedBook.getIsbn());
        book.setCategory(updatedBook.getCategory());
        book.setPublicationYear(updatedBook.getPublicationYear());

        return bookRepo.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new ResourceNotFoundException("Book " + id + " not found");
        }
        bookRepo.deleteById(id);
    }



}
