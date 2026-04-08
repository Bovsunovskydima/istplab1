package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stp.lab1.model.entity.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "(:search IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:search AS String), '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', CAST(:search AS String), '%'))) " +
            "AND (:genre IS NULL OR CAST(b.genre AS String) = :genre)")
    List<Book> findBySearchAndGenre(@Param("search") String search, @Param("genre") String genre);

    // Групування за роком видання
    @Query("SELECT b.publishYear, COUNT(b) FROM Book b WHERE b.publishYear IS NOT NULL GROUP BY b.publishYear ORDER BY b.publishYear")
    List<Object[]> countBooksByPublishYear();

    // Групування за жанром
    @Query("SELECT b.genre, COUNT(b) FROM Book b WHERE b.genre IS NOT NULL GROUP BY b.genre")
    List<Object[]> countBooksByGenre();
}