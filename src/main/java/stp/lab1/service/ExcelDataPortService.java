package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stp.lab1.model.entity.Book;
import stp.lab1.model.enums.BookGenre;
import stp.lab1.model.enums.BookStatus;
import stp.lab1.repository.BookRepository;
import stp.lab1.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelDataPortService {

    private final BookRepository bookRepository;
    private final AdminActionService adminActionService;
    private final UserRepository userRepository;

    public ByteArrayInputStream exportBooksToExcel(String adminEmail) {
        List<Book> allBooks = bookRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            for (BookGenre genre : BookGenre.values()) {
                Sheet sheet = workbook.createSheet(genre.name());

                Row headerRow = sheet.createRow(0);
                String[] columns = {"Назва", "Автор", "Рік видання", "Статус"};
                for (int col = 0; col < columns.length; col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(columns[col]);
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setBold(true);
                    headerStyle.setFont(font);
                    cell.setCellStyle(headerStyle);
                }

                List<Book> genreBooks = allBooks.stream()
                        .filter(b -> b.getGenre() != null && b.getGenre() == genre)
                        .toList();

                int rowIdx = 1;
                for (Book book : genreBooks) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(book.getTitle() != null ? book.getTitle() : "");
                    row.createCell(1).setCellValue(book.getAuthor() != null ? book.getAuthor() : "");
                    row.createCell(2).setCellValue(book.getPublishYear() != null ? book.getPublishYear() : 0);

                    String statusText = (book.getStatus() != null && book.getStatus().name().equals("Available"))
                            ? "Доступно" : "В оренді";
                    row.createCell(3).setCellValue(statusText);
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            workbook.write(out);
            logAdminAction(adminEmail, "Експорт каталогу книг у форматі Excel");
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Помилка при експорті даних в Excel", e);
        }
    }

    public String importBooksFromExcel(MultipartFile file, String adminEmail) {
        int addedCount = 0;
        int skippedCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            List<Book> existingBooks = bookRepository.findAll();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                BookGenre genre;
                try {
                    genre = BookGenre.valueOf(sheet.getSheetName());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String title = row.getCell(0) != null ? row.getCell(0).getStringCellValue().trim() : "";
                    String author = row.getCell(1) != null ? row.getCell(1).getStringCellValue().trim() : "";

                    if (title.isBlank() || author.isBlank()) {
                        skippedCount++;
                        continue;
                    }

                    int year = 2000;
                    if (row.getCell(2) != null) {
                        try {
                            year = (int) row.getCell(2).getNumericCellValue();
                        } catch (Exception ignored) {}
                    }

                    boolean exists = existingBooks.stream()
                            .anyMatch(b -> b.getTitle().equalsIgnoreCase(title) && b.getAuthor().equalsIgnoreCase(author));

                    if (!exists) {
                        Book newBook = new Book();
                        newBook.setTitle(title);
                        newBook.setAuthor(author);
                        newBook.setPublishYear(year);
                        newBook.setGenre(genre);
                        newBook.setStatus(BookStatus.Available);
                        bookRepository.save(newBook);
                        existingBooks.add(newBook);
                        addedCount++;
                    } else {
                        skippedCount++;
                    }
                }
            }
            String resultMessage = String.format("Успішно імпортовано: %d. Пропущено (дублікати або пусті дані): %d.", addedCount, skippedCount);
            logAdminAction(adminEmail, "Імпорт книг з Excel. Результат: " + resultMessage);
            return resultMessage;

        } catch (IOException e) {
            return "Помилка читання файлу: " + e.getMessage();
        }
    }

    private void logAdminAction(String adminEmail, String description) {
        if (adminEmail != null) {
            userRepository.findByEmail(adminEmail).ifPresent(admin ->
                    adminActionService.logAction(admin, description)
            );
        }
    }
}