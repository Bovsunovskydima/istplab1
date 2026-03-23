package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import stp.lab1.model.entity.User;
import stp.lab1.service.BorrowService;
import stp.lab1.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/borrow")
@RequiredArgsConstructor
public class BorrowController {
    private final BorrowService borrowService;
    private final UserService userService;

    @PostMapping("/take/{bookId}")
    public String take(@PathVariable Long bookId, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        borrowService.borrowBook(currentUser.getId(), bookId);
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/return/{recordId}")
    public String returnBook(@PathVariable Long recordId) {
        Long bookId = borrowService.returnBook(recordId);
        return "redirect:/books/" + bookId;
    }
}

