package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stp.lab1.model.CountByGenreDTO;
import stp.lab1.model.CountByYearDTO;
import stp.lab1.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartApiController {

    private final BookService bookService;

    @GetMapping("/booksByYear")
    public List<CountByYearDTO> getCountByYear() {
        return bookService.getBooksCountByYear();
    }

    @GetMapping("/booksByGenre")
    public List<CountByGenreDTO> getCountByGenre() {
        return bookService.getBooksCountByGenre();
    }
}