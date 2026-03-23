package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stp.lab1.model.entity.BorrowRecord;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);
    List<BorrowRecord> findByBookId(Long bookId);
    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);
}
