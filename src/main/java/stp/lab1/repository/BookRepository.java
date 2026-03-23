package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stp.lab1.model.entity.Book;
import stp.lab1.model.enums.BookGenre;
import stp.lab1.model.enums.BookStatus;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatus(BookStatus status);
    List<Book> findByGenre(BookGenre genre);

    @Query("SELECT b FROM Book b WHERE " +
           "(:search IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:genre IS NULL OR b.genre = :genre)")
    List<Book> findBySearchAndGenre(
        @Param("search") String search,
        @Param("genre") BookGenre genre
    );
}
