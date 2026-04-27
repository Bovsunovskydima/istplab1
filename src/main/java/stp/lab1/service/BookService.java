package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stp.lab1.model.CountByGenreDTO;
import stp.lab1.model.CountByYearDTO;
import stp.lab1.model.entity.Book;
import stp.lab1.model.entity.User;
import stp.lab1.model.enums.BookGenre;
import stp.lab1.repository.BookRepository;
import stp.lab1.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AdminActionService adminActionService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Book> findAll(String search, BookGenre genre) {
        String searchParam = (search == null || search.isBlank()) ? null : search;
        String genreParam = (genre == null) ? null : genre.name();
        return bookRepository.findBySearchAndGenre(searchParam, genreParam);
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    public Book save(Book book, String adminEmail) {
        Book savedBook = bookRepository.save(book);
        logAdminAction(adminEmail, "Додано нову книгу: " + savedBook.getTitle());
        return savedBook;
    }

    public Book update(Long id, Book updated, String adminEmail) {
        Book book = findById(id);
        book.setTitle(updated.getTitle());
        book.setAuthor(updated.getAuthor());
        book.setPublishYear(updated.getPublishYear());
        book.setGenre(updated.getGenre());

        Book savedBook = bookRepository.save(book);
        logAdminAction(adminEmail, "Оновлено книгу: " + savedBook.getTitle() + " (ID: " + id + ")");
        return savedBook;
    }

    public void delete(Long id, String adminEmail) {
        Book book = findById(id);
        String bookTitle = book.getTitle();

        bookRepository.deleteById(id);
        logAdminAction(adminEmail, "Видалено книгу: " + bookTitle + " (ID: " + id + ")");
    }

    public List<CountByYearDTO> getBooksCountByYear() {
        return bookRepository.countBooksByPublishYear().stream()
                .map(row -> new CountByYearDTO(String.valueOf(row[0]), (Long) row[1]))
                .toList();
    }

    public List<CountByGenreDTO> getBooksCountByGenre() {
        return bookRepository.countBooksByGenre().stream()
                .map(row -> new CountByGenreDTO(row[0].toString(), (Long) row[1]))
                .toList();
    }

    private void logAdminAction(String adminEmail, String description) {
        if (adminEmail != null) {
            userRepository.findByEmail(adminEmail).ifPresent(admin ->
                    adminActionService.logAction(admin, description)
            );
        }
    }
}