package soyosa.inventaire.Service;

import org.springframework.stereotype.Service;
import soyosa.inventaire.Model.Book;
import soyosa.inventaire.Model.Supply;
import soyosa.inventaire.Repo.BookRepo;
import soyosa.inventaire.Repo.SupplyRepo;

import java.time.LocalDate;
import java.util.List;

@Service
public class SupplyService {

    private final BookService bookService;
    private final SupplyRepo supplyRepo;
    private final BookRepo bookRepo;

    public SupplyService(BookService bookService, SupplyRepo supplyRepo, BookRepo bookRepo) {
        this.bookService = bookService;
        this.supplyRepo = supplyRepo;
        this.bookRepo = bookRepo;
    }

    public Supply addSupply(Supply supply) {

        Book book = bookService.getBookById(supply.getBookId());

        int newQuantity = book.getQuantity() + supply.getQuantityDelivered();
        book.setQuantity(newQuantity);

        bookRepo.save(book);

        supply.setDeliveryDate(LocalDate.now());

        return supplyRepo.save(supply);
    }

    public List<Supply> getSuppliesByBook(Long bookId) {
        return supplyRepo.findByBookId(bookId);
    }

}
