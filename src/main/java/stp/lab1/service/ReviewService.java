package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stp.lab1.model.entity.Book;
import stp.lab1.model.entity.Review;
import stp.lab1.model.entity.User;
import stp.lab1.repository.BookRepository;
import stp.lab1.repository.ReviewRepository;
import stp.lab1.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public void addReview(Long bookId, Long userId, int rating, String comment) {
        if (reviewRepository.existsByUserIdAndBookId(userId, bookId))
            throw new RuntimeException("You already reviewed this book");

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        reviewRepository.save(Review.builder()
            .user(user).book(book)
            .rating(rating).comment(comment).build());
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Review> findByBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @Transactional(readOnly = true)
    public Double avgRating(Long bookId) {
        return reviewRepository.avgRatingByBookId(bookId);
    }
}
