package soyosa.inventaire.Service;

import org.springframework.stereotype.Service;
import soyosa.inventaire.Model.Book;
import soyosa.inventaire.Repo.BookRepo;

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

    public Book getBookById(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
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
        bookRepo.deleteById(id);
    }



}
