package soyosa.inventaire.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soyosa.inventaire.Model.Book;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);


}
