package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stp.lab1.model.entity.Book;
import stp.lab1.model.enums.BookGenre;
import stp.lab1.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

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

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book updated) {
        Book book = findById(id);
        book.setTitle(updated.getTitle());
        book.setAuthor(updated.getAuthor());
        book.setPublishYear(updated.getPublishYear());
        book.setGenre(updated.getGenre());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}