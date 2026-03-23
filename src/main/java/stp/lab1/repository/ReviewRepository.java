package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stp.lab1.model.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
    List<Review> findByUserId(Long userId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double avgRatingByBookId(@Param("bookId") Long bookId);
}
