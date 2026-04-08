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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelDataPortService {

    private final BookRepository bookRepository;

    public ByteArrayInputStream exportBooksToExcel() {
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

                List<Book> genreBooks = allBooks.stream().filter(b -> b.getGenre() == genre).toList();

                int rowIdx = 1;
                for (Book book : genreBooks) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(book.getTitle());
                    row.createCell(1).setCellValue(book.getAuthor());
                    row.createCell(2).setCellValue(book.getPublishYear() != null ? book.getPublishYear() : 0);
                    row.createCell(3).setCellValue(book.getStatus().name() == "Available" ? "Доступно" : "В оренді");
                }
                
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Помилка при експорті даних в Excel", e);
        }
    }


    public void importBooksFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                BookGenre genre;
                try {
                    genre = BookGenre.valueOf(sheetName);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String title = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                    String author = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                    int year = row.getCell(2) != null ? (int) row.getCell(2).getNumericCellValue() : 2000;

                    if (title.isBlank() || author.isBlank()) continue;

                    boolean exists = bookRepository.findAll().stream()
                            .anyMatch(b -> b.getTitle().equalsIgnoreCase(title) && b.getAuthor().equalsIgnoreCase(author));

                    if (!exists) {
                        Book newBook = new Book();
                        newBook.setTitle(title);
                        newBook.setAuthor(author);
                        newBook.setPublishYear(year);
                        newBook.setGenre(genre);
                        newBook.setStatus(BookStatus.Available);
                        bookRepository.save(newBook);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Помилка при читанні Excel файлу", e);
        }
    }
}