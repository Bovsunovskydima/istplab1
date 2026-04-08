package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stp.lab1.model.entity.Book;
import stp.lab1.model.entity.BorrowRecord;
import stp.lab1.model.entity.User;
import stp.lab1.model.enums.BookGenre;
import stp.lab1.model.enums.BookStatus;
import stp.lab1.service.*;

import java.security.Principal;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final BorrowService borrowService;
    private final ExcelDataPortService excelDataPortService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) BookGenre genre,
                       Model model) {
        model.addAttribute("books", bookService.findAll(search, genre));
        model.addAttribute("genres", BookGenre.values());
        model.addAttribute("search", search);
        model.addAttribute("selectedGenre", genre);
        return "books/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Principal principal) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewService.findByBook(id));
        model.addAttribute("avgRating", reviewService.avgRating(id));

        Long activeRecordId = null;
        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getName());
            activeRecordId = userService.getBorrowHistory(currentUser.getId()).stream()
                    .filter(r -> r.getBook().getId().equals(id) && r.getReturnDate() == null)
                    .map(BorrowRecord::getId)
                    .findFirst()
                    .orElse(null);
        }
        model.addAttribute("activeRecordId", activeRecordId);

        if (book.getStatus() == stp.lab1.model.enums.BookStatus.Borrowed) {
            model.addAttribute("currentBorrower", borrowService.getCurrentBorrower(id));
        }

        return "books/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("genres", BookGenre.values());
        model.addAttribute("statuses", BookStatus.values());
        return "books/form";
    }

    @PostMapping
    public String create(@ModelAttribute Book book) {
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("genres", BookGenre.values());
        model.addAttribute("statuses", BookStatus.values());
        return "books/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Book book) {
        bookService.update(id, book);
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<InputStreamResource> exportExcel() {
        String filename = "library_books_" + java.time.LocalDate.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelDataPortService.exportBooksToExcel());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('Admin')")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            excelDataPortService.importBooksFromExcel(file);
        }
        return "redirect:/books";
    }
}
