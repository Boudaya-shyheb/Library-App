package soyosa.inventaire.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soyosa.inventaire.Model.Book;
import soyosa.inventaire.Repo.BookRepo;
import soyosa.inventaire.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    @Test
    void getBookById_throwsWhenNotFound() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(99L));
    }

    @Test
    void addBook_initializesQuantityToZero() {
        Book book = new Book();
        book.setQuantity(33);
        when(bookRepo.save(book)).thenAnswer(invocation -> invocation.getArgument(0));
        Book saved = bookService.addBook(book);
        org.junit.jupiter.api.Assertions.assertEquals(0, saved.getQuantity());
    }
}
