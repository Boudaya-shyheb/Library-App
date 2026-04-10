package soyosa.inventaire.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soyosa.inventaire.Model.Supply;

import java.util.List;

@Repository
public interface SupplyRepo extends JpaRepository<Supply, Long> {

    List<Supply> findByBookId(Long bookId);

}
