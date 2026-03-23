package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stp.lab1.model.entity.Book;
import stp.lab1.model.entity.BorrowRecord;
import stp.lab1.model.entity.User;
import stp.lab1.model.enums.BookStatus;
import stp.lab1.repository.BookRepository;
import stp.lab1.repository.BorrowRecordRepository;
import stp.lab1.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public void borrowBook(Long userId, Long bookId) {
        if (borrowRecordRepository.existsByBookIdAndReturnDateIsNull(bookId))
            throw new RuntimeException("Book is already borrowed");

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setStatus(BookStatus.Borrowed);
        bookRepository.save(book);

        borrowRecordRepository.save(BorrowRecord.builder()
            .user(user).book(book).build());
    }

    public Long returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setReturnDate(LocalDateTime.now());
        borrowRecordRepository.save(record);

        Book book = record.getBook();
        book.setStatus(BookStatus.Available);
        bookRepository.save(book);

        return record.getBook().getId();
    }

    @Transactional(readOnly = true)
    public User getCurrentBorrower(Long bookId) {
        return borrowRecordRepository.findByBookIdAndReturnDateIsNull(bookId)
                .map(BorrowRecord::getUser)
                .orElse(null);
    }
}
